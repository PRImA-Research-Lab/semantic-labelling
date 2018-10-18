JAVA_SOURCES = $(shell find -name '*.java'|grep -v '/test/')
CLASSPATH = src:$(shell find -name '*.jar'|paste -s -d:)
JAVAC = javac -cp "$(CLASSPATH)"
JAVA = java -cp "$(CLASSPATH)"

# BEGIN-EVAL makefile-parser --make-help Makefile

help:
	@echo ""
	@echo "  Targets"
	@echo ""
	@echo "    build            Build all java code"
	@echo "    clean            Clean up built classes"
	@echo "    ontology-editor  Run OntologyEditor"
	@echo "    repository-hub   Run RepositoryHub"
	@echo "    workflow-editor  Run WorkflowEditor"
	@echo ""
	@echo "  Variables"
	@echo ""

# END-EVAL

.SUFFIXES: .java .class
.java.class:
	$(JAVAC) $*.java

# Build all java code
build: $(JAVA_SOURCES:.java=.class)

# Clean up built classes
clean:
	@rm -vf $(JAVA_SOURCES:.java=.class)

# Run OntologyEditor
ontology-editor: build
	$(JAVA) org.primaresearch.clc.phd.ontology.gui.OntologyEditor

# Run RepositoryHub
repository-hub: build
	$(JAVA) org.primaresearch.clc.phd.repository.gui.RepositoryHub

# Run WorkflowEditor
workflow-editor: build
	$(JAVA) org.primaresearch.clc.phd.workflow.gui.WorkflowEditor
