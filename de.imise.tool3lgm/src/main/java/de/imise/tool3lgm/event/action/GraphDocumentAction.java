package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;

/**
 * Eine Aktion, die enabled ist, wenn irgendein Teilmodell oder ein Gesamtmodell
 * aktiv (also geöffnet) ist.
 *
 * @author AXS
 */
public class GraphDocumentAction extends StaticAction {

    /**
     * Wenn <code>true</code> wird das aktive doc aus dem TemplateContext
     * genommen, sonst das aus Static.
     */
    private final boolean templateContextAction;

    /**
     * @param identifier
     */
    public GraphDocumentAction(final Object identifier) {
        super(identifier);
        templateContextAction = false;
    }

    /**
     * @param identifier
     * @param templateContextAction
     */
    public GraphDocumentAction(final Object identifier, final boolean templateContextAction) {
        super(identifier);
        this.templateContextAction = templateContextAction;
    }

    /**
     * @param identifier
     * @param arguments
     * @param text
     * @param textSuffix
     */
    public GraphDocumentAction(final Object identifier, final String arguments, final String text, final String textSuffix) {
        super(identifier, arguments, text, textSuffix);
        templateContextAction = false;
    }

    /**
     * @param identifier
     * @param textSuffix
     */
    public GraphDocumentAction(final Object identifier, final String textSuffix) {
        super(identifier, textSuffix);
        templateContextAction = false;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && getActiveDoc() != null;
    }

    /**
     * @return
     */
    protected GraphDocument getActiveDoc() {
        if (templateContextAction) {
            TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
            GraphDocument activeTemplate = templateLibrariesManager.getActiveTemplate();
            return activeTemplate;
        }
        return Static.getSelectedDoc();
    }

}
