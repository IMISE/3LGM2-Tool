package de.imise.tool3lgm.graphtools.dialog.panel;
/**
 * @author AXS
 * created on 20.05.2007
 */
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
/**
 * Mit diesem Panel zeigen Aufgaben ihre Anwendungsbausteinkonfigurationen an.
 * 
 */
public class AufAwbKonfPanel extends LGMDragNDropPanel {
	
	private LGMDragNDropTree ltree;
	private LGMDragNDropTree rtree;
	private DefaultTreeModel model, abmodel;
	private LGMTreeNode root, abroot;
	private JLabel label2, redundanzLabel;
	private JScrollPane sp2;
	private JPanel buttonpanel;
	private static String teilmodell = Tool3lgmConstants.getResString("submodel");
	private static String gesamtmodell = Tool3lgmConstants.getResString("whole_model");
	private static String redundanteKonfs = Tool3lgmConstants.getResString("redundante_Konfigs");

	private LGMAction addAction;
	private LGMAction removeAction;
	
	
	public AufAwbKonfPanel(ElementPropertyDialog pd) {
		super(pd);
		setPreferredSize(new Dimension(550, 350));
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints constraints = new GridBagConstraints();

		redundanzLabel = new JLabel();

		JLabel label = new JLabel(Tool3lgmConstants.getResString("ABKonfiguration_p"));
		root = new LGMTreeNode(getModelElement().getName(), false);
		model = new DefaultTreeModel(root);
		ltree = new LGMDragNDropTree(model, mainDoc);
		ltree.setRootVisible(false);
		ltree.setShowsRootHandles(true);
		ltree.setCellRenderer(treeRenderer);
		ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		JScrollPane sp = new JScrollPane(ltree);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.ipadx = -30;
		constraints.ipady = -10;
		add(this, this.viewButton, constraints, 0, 5, 1, 1);
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.anchor = GridBagConstraints.WEST;
		add(this, label, constraints, 0, 0, 1, 1);
		add(this, redundanzLabel, constraints, 0, 5, 5, 1);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, sp, constraints, 0, 1, 1, 4);

		label2 = new JLabel(Tool3lgmConstants.getResString("Anwendungsbaustein_p"));
		abroot = new LGMTreeNode(Tool3lgmConstants.getResString("Anwendungsbaustein_p"), false);
		abmodel = new DefaultTreeModel(abroot);
		rtree = new LGMDragNDropTree(abmodel, mainDoc);
		rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		rtree.setRootVisible(false);
		rtree.setShowsRootHandles(true);
		rtree.setCellRenderer(treeRenderer);
		sp2 = new JScrollPane(rtree);
		

		
		
		/*
		 * Start: MouseListener erstellen und an Trees anhängen
		 * ...
		 */
		LGMAction ltreeMouseAction = LGMActionLibrary.getMouseAction(ltree, this);
		LGMAction rtreeMouseAction = LGMActionLibrary.getMouseAction(rtree, this);

		ltree.addMouseListener(new LGMMouseListener(null, null, null, ltreeMouseAction, null));
		rtree.addMouseListener(new LGMMouseListener(null, null, null, rtreeMouseAction, null));
		/*
		 * ...
		 * End: MouseListener erstellen und an Trees anhängen
		 */
		
		/*
		 * Start: TreeSelectionListener erstellen und an Trees anhängen
		 * ...
		 */
		LGMAction ltreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(ltree, this);
		LGMAction rtreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rtree, this);

		ltree.addTreeSelectionListener(new LGMTreeSelectionListener(ltreeSelectionAction));
		rtree.addTreeSelectionListener(new LGMTreeSelectionListener(rtreeSelectionAction));
		/*
		 * ...
		 * End: TreeSelectionListener erstellen und an Trees anhängen
		 */
		
		/*
		 * Start: Buttons & Actions erstellen, Actions setzen
		 * ...
		 */
		JButton addButton = new JButton();
		JButton removeButton = new JButton();
		
		try {
			addAction = LGMActionLibrary.getAddElementAction(rtree, ltree, this, false);
			removeAction = LGMActionLibrary.getDisconnectAction(ltree, rtree, this, false);
		}
		catch (ActionNotDefinedForClassException e) {
			Log.log(Log.DEBUG,e.getMessage());
		}
		addButton.setAction(addAction);
		removeButton.setAction(removeAction);
		/*
		 * ...
		 * end: Buttons & Actions erstellen, Actions setzen
		 */
		
		buttonpanel = new JPanel();
		buttonpanel.setSize(30, 250);
		buttonpanel.setLayout(new GridLayout(3, 1));
		buttonpanel.add(addButton);
		buttonpanel.add(removeButton);
		
		init();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#init()
	 */
	@Override
	public void init() {
		super.init();
		remove(buttonpanel);
		remove(label2);
		remove(sp2);
		buildTree();
		revalidate();
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#showFullDialog()
	 */
	@Override
	public void showFullDialog() {
		super.showFullDialog();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		add(this, buttonpanel, constraints, 1, 3, 1, 2);
		constraints.anchor = GridBagConstraints.WEST;
		add(this, label2, constraints, 2, 0, 1, 1);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, sp2, constraints, 2, 1, 1, 4);

		buildABTree();

		revalidate();
		repaint();
	}
	
	/**
	 * 
	 */
	private void buildTree() {
		ltree.saveExpansion();
		ltree.saveSelection();
		root.removeAllChildren();
		ltree.reset();
		
		
		ArrayList<ElementContainer> all = getModelElement().getConnectedContainer(AufOrgKombination.class, mainDoc);
		//nur Knoten für Elemente in der all-Liste bis zur Größe der direkt vrbundenen dürfen am Ende
		//selektierbar sein
		int firstNonSelectableIndex = all.size();
		if (UserProperties.isSearchParts())
			all.addAll(getModelElement().getPartConnectedContainer(AufOrgKombination.class, mainDoc));
		if (UserProperties.isSearchParents())
			all.addAll(getModelElement().getParentConnectedContainer(AufOrgKombination.class, mainDoc));
		for (ElementContainer aufOrg : all) {
			LGMTreeNode node = new LGMTreeNode(aufOrg, false);
			root.add(node);
			for (ElementContainer abKonf : aufOrg.getElement().getConnectedContainer(ABKonfiguration.class, mainDoc)) {
				LGMTreeNode abkonf = new LGMTreeNode(abKonf, false);
				node.add(abkonf);
				for (ElementContainer awb : abKonf.getElement().getConnectedContainer(Anwendungsbaustein.class, doc))
					ltree.addObject(awb, abkonf, null, true, false, false);
			}
			//alle Elemente die von den Parts oder Parents kamen, nichtselektierbar setzen
			if (root.getChildCount()-1>=firstNonSelectableIndex)
				node.setSelectable(false);
		}

		model.reload();
		ltree.restoreExpansion();
		ltree.restoreSelection();
		
		StringBuilder sb = new StringBuilder(60);
		sb.append(redundanteKonfs);
		sb.append(" ");
		if (doc instanceof Szenario) {
			sb.append(((Aufgabe)getModelElement()).getAllDifferentKonfigs(doc).size() - 1);
			sb.append(" (");
			sb.append(teilmodell);
			sb.append(") ");
		}
		sb.append(((Aufgabe)getModelElement()).getAllDifferentKonfigs(mainDoc).size() - 1);
		sb.append(" (");
		sb.append(gesamtmodell);
		sb.append(")");
		redundanzLabel.setText(sb.toString());
	}

	/**
	 * 
	 */
	private void buildABTree() {
		rtree.saveExpansion();
		rtree.saveSelection();
		abroot.removeAllChildren();
		rtree.reset();
		for (ElementContainer ec : mainDoc.getElementContainer(Anwendungsbaustein.class, true, true))
			rtree.addObject(ec, abroot, null, false, true);
		abmodel.reload();
		rtree.restoreExpansion();
		rtree.restoreSelection();
	}

	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#collectDragNDropActionChains()
	 */
	@Override
	protected DragNDropActionChain[] collectDragNDropActionChains() {
		DragNDropActionChain dndAC1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
		DragNDropActionChain dndAC2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);
		
		return new DragNDropActionChain[] {dndAC1,dndAC2};
		
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#getAllDragNDropTrees()
	 */
	@Override
	public LGMDragNDropTree[] getAllDragNDropTrees() {
		return new LGMDragNDropTree[] {rtree, ltree};
	}

}
