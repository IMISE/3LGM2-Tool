package de.imise.tool3lgm.graphtools;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;

/**
 * @author AXS (16.09.2021)
 */
public class ModelCopyAndPasteHandler {

    /**  */
    private static GDCollection clipboardGDCollection;

    /**
     *
     */
    public static void copy() {
        LGMGraphDocument sourceDoc = Static.getSelectedDoc();
        if (sourceDoc == null) {
            return;
        }
        GDCollection sourceCollection = sourceDoc.getCollection();
        sourceCollection.resetPasteCounter();
        MetaModelContext metaModelcontext = sourceCollection.getMetaModelContext();
        Tool3lgmModelType clipboardModelType = new Tool3lgmModelType(metaModelcontext, ModelCategory.CLIPBOARD);
        clipboardGDCollection = new GDCollection(clipboardModelType, false); //TODO: add clear() to GDCollection if modeltype is the same as last in copy&paste
        //copy in a szenario -> create a szenario in the target collection and select it
        if (sourceDoc instanceof Szenario) {
            Szenario szen = clipboardGDCollection.createSzenario();
            clipboardGDCollection.setSelectedDoc(szen);
        }
        LGMGraphDocument targetDoc = clipboardGDCollection.getSelectedDoc();
        LGMGraphDocument.copySelectedToModel(sourceDoc, targetDoc, STANDARD_PID);
    }

    /**
     *
     */
    public static void paste() {
        if (!canPaste()) {
            return;
        }
        LGMGraphDocument targetDoc = Static.getSelectedDoc();
        GDCollection targetCollection = targetDoc.getCollection();
        LGMGraphDocument sourceDoc = clipboardGDCollection.getSelectedDoc();
        LGMGraphDocument.copySelectedToModel(sourceDoc, targetDoc, STANDARD_PID);
        targetCollection.increasePasteCounter();
    }

    /**
     * @return <code>true</code> if this handler has pasable content for the
     *         currently selected model
     */
    public static boolean canPaste() {
        if (clipboardGDCollection == null) {
            return false;
        }
        LGMGraphDocument targetDoc = Static.getSelectedDoc();
        if (targetDoc == null) {
            return false;
        }
        return true;
    }

}
