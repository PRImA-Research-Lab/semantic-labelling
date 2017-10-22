package org.primaresearch.clc.phd.workflow.gui.panel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.primaresearch.clc.phd.workflow.Workflow;
import org.primaresearch.clc.phd.workflow.activity.Activity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity;
import org.primaresearch.clc.phd.workflow.activity.DirectedGraphActivity.ActivityNode;
import org.primaresearch.clc.phd.workflow.activity.ForLoopActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity;
import org.primaresearch.clc.phd.workflow.activity.ifelse.IfElseActivity.IfBranch;
import org.primaresearch.clc.phd.workflow.data.port.DataPort;
import org.primaresearch.clc.phd.workflow.data.port.InputPort;
import org.primaresearch.clc.phd.workflow.data.port.OutputPort;

/**
 * Experimental UML renderer for a workflow
 * 
 * @author clc
 *
 */
public class WorkflowUmlView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Workflow workflow;
	private final static float dash1[] = {8.0f};
    private final static BasicStroke dashed = new BasicStroke(	1.0f,
										                        BasicStroke.CAP_BUTT,
										                        BasicStroke.JOIN_MITER,
										                        8.0f, dash1, 0.0f);
    private Map<Activity, Rectangle> activityRects = new HashMap<Activity, Rectangle>();
    private Map<DataPort, Rectangle> portRects = new HashMap<DataPort, Rectangle>();

	public WorkflowUmlView(Workflow workflow) {
		this.workflow = workflow;
		setLayout(new BorderLayout(0, 0));
	}

	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        
        setBackground(Color.WHITE);
        
        try {
	        //Activities
	        Dimension size = getSize();
	        Rectangle boundingBox = new Rectangle(size);
	        drawActivity(g2d, boundingBox, workflow.getRootActivity(), 255);
	        
	        //Ports
	        for (Iterator<Activity> it = activityRects.keySet().iterator(); it.hasNext(); ) {
	        	Activity act = it.next();
	        	drawActivityPorts(g2d, act, activityRects.get(act));
	        }
        } catch (Exception exc) {
        	exc.printStackTrace();
        }
    }
	
	private void drawActivity(Graphics2D g2d, Rectangle boundingBox, Activity activity, int greyTone) {
		if (activity == null)
			return;
		
		int borderX = (int)((double)boundingBox.width * 0.02 + 0.5);
		int borderY = (int)((double)boundingBox.height * 0.02 + 0.5);

		//Frame
		drawActivityFrame(g2d, activity, boundingBox.x + borderX, boundingBox.y + borderY, 
				boundingBox.width - 2 * borderX, boundingBox.height - 2 * borderY, 
				Math.max(borderX, borderY),
				(int)((double)boundingBox.height * 0.0175 + 0.5), activity.getCaption(),
				countChildren(activity) == 0, activity instanceof ForLoopActivity,
				greyTone);
		
		
		//Children
		final int padding = 2;
		Rectangle innerBox = new Rectangle(	boundingBox.x + 3 * borderX + padding, boundingBox.y + 3 * borderY + padding, 
											boundingBox.width - 6 * borderX - padding, boundingBox.height - 6 * borderY - padding);
		// For loop
		if (activity instanceof ForLoopActivity) {
			drawActivity(g2d, innerBox, ((ForLoopActivity)activity).getChildActivity(), greyTone - 10);
		}
		// If-else
		else if (activity instanceof IfElseActivity) {
			IfElseActivity ifelse = (IfElseActivity)activity;
			//Vertical stack
			int weightSum = 0;
			for (Iterator<IfBranch> it = ifelse.getBranches().iterator(); it.hasNext(); )
				weightSum += 1 + countChildrenRecursively(it.next().getActivity());
			
			int heightSum = 0;
			for (Iterator<IfBranch> it = ifelse.getBranches().iterator(); it.hasNext(); ) {
				Activity child = it.next().getActivity();
				int weight = 1 + countChildrenRecursively(child);
				int childHeight = (int)((double)weight / (double)weightSum * (double)innerBox.height);
				drawActivity(g2d, new Rectangle(innerBox.x, innerBox.y + heightSum, innerBox.width, childHeight), 
							child, greyTone - 10);
				heightSum += childHeight;
			}
		}
		// Directed graph
		else if (activity instanceof DirectedGraphActivity) {
			DirectedGraphActivity dag = (DirectedGraphActivity)activity;
			if (dag.getGraphNodes() != null) {
				//Horizontal stack
				int weightSum = 0;
				for (Iterator<Activity> it = dag.getChildActivities().iterator(); it.hasNext(); )
					weightSum += 1 + countChildrenRecursively(it.next());
				
				//Sort by x
				List<ActivityNode> sortedGraphNodes = new ArrayList<ActivityNode>();
				sortedGraphNodes.addAll(dag.getGraphNodes());
				Collections.sort(sortedGraphNodes, new Comparator<ActivityNode>() {
					@Override
					public int compare(ActivityNode n1, ActivityNode n2) {
						return n1.getPosX() - n2.getPosX();
					}
				});
				
				int widthSum=0;
				for (Iterator<ActivityNode> it = sortedGraphNodes.iterator(); it.hasNext(); ) {
					Activity child = it.next().getActivity();
					int weight = 1 + countChildrenRecursively(child);
					int childWidth = (int)((double)weight / (double)weightSum * (double)innerBox.width);
					drawActivity(g2d, new Rectangle(innerBox.x + widthSum, innerBox.y, childWidth, innerBox.height), 
							child, greyTone - 10);
					widthSum += childWidth;
				}
			}
		}
	}
	
	/**
	 * Draws rounded frame and label of an activity
	 */
	private void drawActivityFrame(Graphics2D g2d, Activity activity, int x, int y, int width, int height, int arcSize,
									int fontSize, String caption,
									boolean square, boolean dash, int greyTone) {
		
		if (dash)
			g2d.setStroke(dashed);
		
		//Make it square?
		if (square) {
			int size = Math.min(width,  height);
			x += (width - size) / 2;
			y += (height - size) / 2;
			width = size;
			height = size;
		}
		
		activityRects.put(activity, new Rectangle(x, y, width, height));
	
		//Rounded rectangle
		// Fill
		g2d.setColor(new Color(greyTone-20, greyTone-10, greyTone));
		g2d.fillRoundRect(x, y, width, height, arcSize, arcSize);
		// Line
		g2d.setColor(Color.BLACK);
		g2d.drawRoundRect(x, y, width, height, arcSize, arcSize);

		g2d.setStroke(new BasicStroke());

		//Label
		g2d.setFont(new Font("Calibri", Font.PLAIN, fontSize + (countChildren(activity) == 0 ? 4 : 0)));
		g2d.setClip(x + arcSize/2, y, 
					width - arcSize, 2 * arcSize);
		g2d.drawString(caption, x + arcSize/2, y + arcSize);
		
		g2d.setClip(null);
	}
	
	private void drawActivityPorts(Graphics2D g2d, Activity activity, Rectangle activityRect) {
		
		//Rectangles
		// Input ports
		drawActivityPorts(g2d, activity, activity.getInputPorts(), activityRect.x, 
				activityRect.y + activityRect.height / 10,  activityRect.y + activityRect.height - activityRect.height / 10);

		// Output ports
		drawActivityPorts(g2d, activity, activity.getOutputPorts(), activityRect.x + activityRect.width + 1, 
				activityRect.y + activityRect.height / 10,  activityRect.y + activityRect.height - activityRect.height / 10);
		
		
		//Links
		// Input ports
		for (Iterator<InputPort> it = activity.getInputPorts().iterator(); it.hasNext(); )
			drawInputPortLink(g2d, it.next());
		// Output ports
		for (Iterator<OutputPort> it = activity.getOutputPorts().iterator(); it.hasNext(); )
			drawOutputPortLink(g2d, it.next());
	}
	
	private void drawActivityPorts(Graphics2D g2d, Activity activity, List<? extends DataPort> ports, int x, int yMin, int yMax) {
		
		if (ports == null)
			return;
		
		//Rectangles
		int portHeight = (yMax - yMin) / 10;
		int heightSum = 0;
		boolean hasChildren = countChildren(activity) > 0;

		g2d.setFont(new Font("Calibri", Font.PLAIN, portHeight / 5 + (!hasChildren ? 4 : 0)));

		for (Iterator<? extends DataPort> it = ports.iterator(); it.hasNext(); ) {
			DataPort port = it.next();
			g2d.fillRect(x-1, yMin+heightSum, 2, portHeight);
			
			portRects.put(port, new Rectangle(x-1, yMin+heightSum, 2, portHeight));
			
			//Label
			if (port instanceof InputPort) {
				g2d.clipRect(x-portHeight/3, yMin+heightSum, portHeight + (!hasChildren ? portHeight : 0), portHeight);
				g2d.drawString(port.getDataObject().getCaption(), Math.max(0, x-portHeight/3), yMin + heightSum + portHeight/3);
				g2d.setClip(null);
			}
			else if (port instanceof OutputPort) {
				int startX = x-2*portHeight/3 - (!hasChildren ? portHeight : 0);
				g2d.clipRect(startX, yMin+heightSum, portHeight + (!hasChildren ? portHeight : 0), portHeight);
				g2d.drawString(port.getDataObject().getCaption(), Math.max(0, startX), yMin + heightSum + 3*portHeight/4);
				g2d.setClip(null);
			}

			heightSum += portHeight + 10;
		}
	}

	private void drawInputPortLink(Graphics2D g2d, InputPort port) {
		if (port == null || !portRects.containsKey(port))
			return;
		
		if (port.getSource() == null || !portRects.containsKey(port.getSource()))
			return;
		Point start = getPortHandle(port);
		Point end = getPortHandle(port.getSource());
		g2d.drawLine(start.x, start.y, end.x, end.y);
	}
	
	private void drawOutputPortLink(Graphics2D g2d, OutputPort port) {
		if (port == null || !portRects.containsKey(port))
			return;
		
		if (port.getForwardedPorts() == null || port.getForwardedPorts().isEmpty())
			return;
		
		for (Iterator<OutputPort> it = port.getForwardedPorts().iterator(); it.hasNext(); ) {
			OutputPort source = it.next();
			if (!portRects.containsKey(source))
				continue;
			Point start = getPortHandle(port);
			Point end = getPortHandle(source);
			g2d.drawLine(start.x, start.y, end.x, end.y);
		}
	}
	
	private Point getPortHandle(DataPort port) {
		Rectangle rect = portRects.get(port);
		return new Point(port instanceof InputPort ? rect.x : rect.x+1, rect.y + rect.height/2);
	}

	/**
	 * Counts the number of direct children of the given activity
	 */
	private int countChildren(Activity activity) {
		if (activity instanceof IfElseActivity)
			return ((IfElseActivity)activity).getBranches().size();
		else if (activity instanceof DirectedGraphActivity)
			return ((DirectedGraphActivity)activity).getChildActivities().size();
		else if (activity instanceof ForLoopActivity)
			return ((ForLoopActivity)activity).getChildActivity() != null ? 1 : 0;
		return 0;
	}
	
	/**
	 * Counts the number of children and children's children of the given activity
	 */
	private int countChildrenRecursively(Activity activity) {
		if (activity == null)
			return 0;
		
		if (activity instanceof IfElseActivity) {
			IfElseActivity ifelse = (IfElseActivity)activity;
			if (ifelse.getChildActivities() == null)
				return 0;
			
			int sum = 0;
			for (Iterator<Activity> it = ifelse.getChildActivities().iterator(); it.hasNext(); )
				sum += countChildrenRecursively(it.next());
			return sum;
		}
		else if (activity instanceof DirectedGraphActivity) {
			DirectedGraphActivity dag = (DirectedGraphActivity)activity;
			if (dag.getChildActivities() == null)
				return 0;
			
			int sum = 0;
			for (Iterator<Activity> it = dag.getChildActivities().iterator(); it.hasNext(); )
				sum += countChildrenRecursively(it.next());
			return sum;
		}
		else if (activity instanceof ForLoopActivity) {
			return 1 + countChildrenRecursively(((ForLoopActivity)activity).getChildActivity());
		}
		return 0;
	}
}
