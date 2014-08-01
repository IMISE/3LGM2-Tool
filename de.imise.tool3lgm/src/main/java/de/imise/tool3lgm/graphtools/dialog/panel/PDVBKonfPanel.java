package de.imise.tool3lgm.graphtools.dialog.panel;
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
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Mit diesem Panel zeigen Anwendungsbausteine die sie über physische
 * Datenverarbeitungsbausteinkonfigurationen verbundenen physische
 * Datenverarbeitungsbausteine an.
 * Dieses Panel unterstützt das Ändern dieser Konfigurationen.
 * 
 * @author N.N.
 *
 */
public class PDVBKonfPanel extends LGMDragNDropPanel {
	private LGMDragNDropTree ltree;
	private LGMDragNDropTree rtree;
	private DefaultTreeModel model, pdvbmodel;
	private LGMTreeNode root, pdvbroot;
	private JLabel label2;
	private JScrollPane sp2;
	private JPanel buttonpanel;
	
	private LGMAction addAction;
	private LGMAction removeAction;

	public PDVBKonfPanel(ElementPropertyDialog pd) {
		super(pd);

		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints constraints = new GridBagConstraints();

		JLabel label = new JLabel(Tool3lgmConstants.getResString("PhysischerDVBaustein_p"));
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
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, sp, constraints, 0, 1, 1, 4);

		label2 = new JLabel(Tool3lgmConstants.getResString("PhysischerDVBaustein_p"));
		pdvbroot = new LGMTreeNode(Tool3lgmConstants.getResString("PhysischerDVBaustein_p"), false);
		pdvbmodel = new DefaultTreeModel(pdvbroot);
		rtree = new LGMDragNDropTree(pdvbmodel, mainDoc);
		rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		rtree.setRootVisible(false);
		rtree.setShowsRootHandles(true);
		rtree.setCellRenderer(treeRenderer);

		sp2 = new JScrollPane(rtree);
		/*
		 * Start: MouseListener erstellen und an Trees anhängen
		 * ...
		 */
		LGMAction ltreeMouseAction = LGMActionLibrary.getMouseAction(ltree,this);
		LGMAction rtreeMouseAction = LGMActionLibrary.getMouseAction(rtree,this);

		ltree.addMouseListener(new LGMMouseListener(null,null,null,ltreeMouseAction, null));
		rtree.addMouseListener(new LGMMouseListener(null,null,null,rtreeMouseAction, null));
		/*
		 * ...
		 * End: MouseListener erstellen und an Trees anhängen
		 */
		
		/*
		 * Start: TreeSelectionListener erstellen und an Trees anhängen
		 * ...
		 */
		LGMAction ltreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(ltree,this);
		ltree.addTreeSelectionListener(new LGMTreeSelectionListener(ltreeSelectionAction));
		/*
		 * ...
		 * End: TreeSelectionListener erstellen und an Trees anhängen
		 */
		
		/*
		 * Start: Buttons & Actions erstellen und registrieren
		 * ...
		 */
		JButton addButton = new JButton();
		JButton removeButton = new JButton();
		
		try {
			this.addAction = LGMActionLibrary.getAddElementAction(this.rtree,this.ltree,this,false);
			this.removeAction = LGMActionLibrary.getDisconnectAction(this.ltree,this.rtree,this,false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		addButton.setAction(this.addAction);
		removeButton.setAction(this.removeAction);
		/*
		 * ...
		 * end: Buttons & Actions erstellen und registrieren
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
	protected void init() {
		super.init();
		
		remove(buttonpanel);
		remove(label2);
		remove(sp2);



		buildTree();
		//expandTree(tree);

		revalidate();
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#showFullDialog()
	 */
	@Override
	protected void showFullDialog() {
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

		buildPDVBTree();
		//expandTree(pdvbtree);

		revalidate();
		repaint();
	}

	/**
	 * 
	 */
	private void buildTree() {
		root.removeAllChildren();
		ltree.reset();
		
		ModelElement modelElement = getModelElement();
		for (ElementContainer ec : modelElement.getConnectedContainer(DBKonfiguration.class, mainDoc)) {
			ArrayList<ElementContainer> server = ((DBKonfiguration) ec.getElement()).getServerContainer(mainDoc);
			LGMTreeNode pdvbkonf = new LGMTreeNode(ec, false);
			root.add(pdvbkonf);
			for (ElementContainer sC : server)
				ltree.addObject(sC, pdvbkonf, null, true, false, false);
		}
		if (UserProperties.isSearchParts()) {
			for (ElementContainer ec : modelElement.getPartConnectedContainer(DBKonfiguration.class, mainDoc)) {
				ArrayList<ElementContainer> server = ((DBKonfiguration) ec.getElement()).getServerContainer(mainDoc);
				LGMTreeNode pdvbkonf = new LGMTreeNode(ec, false);
				pdvbkonf.setSelectable(false);
				root.add(pdvbkonf);
				for (ElementContainer sC : server) {
					LGMTreeNode node = ltree.addObject(sC, pdvbkonf, null, true, false, false);
					if (node != null) {
						node.setSelectable(false);
					}
				}
			}
		}
		if (UserProperties.isSearchParents()) {
			for (ElementContainer ec : modelElement.getParentConnectedContainer(DBKonfiguration.class, mainDoc)) {
				ArrayList<ElementContainer> server = ((DBKonfiguration)ec.getElement()).getServerContainer(mainDoc);
				LGMTreeNode pdvbkonf = new LGMTreeNode(ec, false);
				pdvbkonf.setSelectable(false);
				root.add(pdvbkonf);
				for (ElementContainer sC : server) {
					LGMTreeNode node = ltree.addObject(sC, pdvbkonf, null, true, false, false);
					if (node != null) {
						node.setSelectable(false);
					}
				}
			}
		}
		model.reload();
	}
	
	/**
	 * 
	 */
	private void buildPDVBTree() {
		pdvbroot.removeAllChildren();
		rtree.reset();
		ArrayList<ElementContainer> pdvb = mainDoc.getElementContainer(PhysischerDVBaustein.class);
		for (int m = 0; m < pdvb.size(); m++)
			//pdvbroot.add(new LGMTreeNode(pdvb.get(m), false));
			rtree.addObject(pdvb.get(m), pdvbroot, null, false, true);
		pdvbmodel.reload();
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#collectDragNDropActionChains()
	 */
	@Override
	protected DragNDropActionChain[] collectDragNDropActionChains() {
		DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(this.rtree, this.ltree,this.addAction);
		DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(this.ltree, this.rtree,this.removeAction);
	
		return new DragNDropActionChain[] {tac1,tac2};
	}

	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.LGMDragNDropPanel#getAllDragNDropTrees()
	 */
	@Override
	public LGMDragNDropTree[] getAllDragNDropTrees() {
		return new LGMDragNDropTree[] {this.rtree, this.ltree};
	}

}
