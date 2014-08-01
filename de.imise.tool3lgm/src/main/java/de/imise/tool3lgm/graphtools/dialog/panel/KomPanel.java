package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

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
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

public class KomPanel extends LGMDragNDropPanel {

	private LGMDragNDropTree ltree, rtree;
	private DefaultTreeModel omodel, romodel;
	private LGMTreeNode oroot, roroot;
	private JLabel oben;
	private JPanel buttonpanel;
	private JScrollPane sp2;
	private Class<? extends ModelElement> searchElementClass;
	
	private LGMAction addAction;
	private LGMAction removeAction;

	public KomPanel(Class<? extends ModelElement> searchElementClass, ElementPropertyDialog pd, boolean editable) {
		super(pd);
		this.searchElementClass = searchElementClass;

		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints constraints = new GridBagConstraints();

		// km = kommuniziert mit 
//		oben = new JLabel(Tool3lgmConstants.getResString("km"));
//		oroot = new LGMTreeNode(Tool3lgmConstants.getResString("km"), false);

		oben = new JLabel(ModelConstants.getDisplayableName(searchElementClass));
		oroot = new LGMTreeNode(oben.getText(), false);

		omodel = new DefaultTreeModel(oroot);
		ltree = new LGMDragNDropTree(omodel);
		ltree.setRootVisible(false);
		ltree.setCellRenderer(treeRenderer);
		
		
		

		ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);


		JScrollPane sp1 = new JScrollPane(ltree);
		expandTree(ltree);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.ipadx = -30;
		constraints.ipady = -10;
		if (editable)
			add(this, this.viewButton, constraints, 0, 6, 1, 1);
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.anchor = GridBagConstraints.WEST;
		add(this, oben, constraints, 0, 0, 1, 1);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, sp1, constraints, 0, 1, 1, 1);

		oben = new JLabel(Tool3lgmConstants.getResString("frei"));
		roroot = new LGMTreeNode(Tool3lgmConstants.getResString("frei"), false);
		romodel = new DefaultTreeModel(roroot);
		rtree = new LGMDragNDropTree(romodel);
		rtree.setRootVisible(false);
		rtree.setCellRenderer(treeRenderer);

		rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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
		LGMAction rtreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rtree,this);
		
		ltree.addTreeSelectionListener(new LGMTreeSelectionListener(ltreeSelectionAction));
		rtree.addTreeSelectionListener(new LGMTreeSelectionListener(rtreeSelectionAction));
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
		
		if (oben != null)
			remove(oben);
		if (buttonpanel != null)
			remove(buttonpanel);
		if (sp2 != null)
			remove(sp2);

		oroot.removeAllChildren();
		ModelElement modelElement = getModelElement();
		for (Object ec : modelElement.getConnectedContainer(searchElementClass, mainDoc))
			oroot.add(new LGMTreeNode(ec, false));

		if (UserProperties.isSearchParts()) {
			for (Object ec : modelElement.getPartConnectedContainer(searchElementClass, mainDoc)) {
				LGMTreeNode node = new LGMTreeNode(ec, false);
				node.setSelectable(false);
				oroot.add(node);
			}
		}
		if (UserProperties.isSearchParents()) {
			for (Object ec : modelElement.getParentConnectedContainer(searchElementClass, mainDoc)) {
				LGMTreeNode node = new LGMTreeNode(ec, false);
				node.setSelectable(false);
				oroot.add(node);
			}
		}
		omodel.reload();
		expandTree(ltree);
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

		roroot.removeAllChildren();
		ModelElement modelElement = getModelElement();
		for (ElementContainer ec : mainDoc.getElementContainer(searchElementClass)) {
			ModelElement other = ec.getElement();
			if (modelElement != other)
				if (!modelElement.isConnectedWith(other)) {
					LGMTreeNode node = new LGMTreeNode(ec, false);
					roroot.add(node);
				}
		}
		romodel.reload();
		expandTree(rtree);

		constraints.fill = GridBagConstraints.NONE;
		add(this, buttonpanel, constraints, 1, 1, 1, 2);

		constraints.anchor = GridBagConstraints.WEST;
		add(this, oben, constraints, 2, 0, 1, 1);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 100;
		add(this, sp2, constraints, 2, 1, 1, 1);

		revalidate();
		repaint();
	}

	/*
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		
		
		String str = e.getActionCommand();
		if (str.startsWith("verbinden")) {
			int selrows = rtree.getSelectionCount();
			if (selrows > 0) {
				for (int n = 0; n < selrows; n++) {
					if (((LGMTreeNode) rtree.getLastSelectedPathComponent()).getUserObject() instanceof String)
						return;
					LGMTreeNode node = (LGMTreeNode) rtree.getLastSelectedPathComponent();
					NodeContainer knot = (NodeContainer) node.getUserObject();
					doc.link(modelElement, knot.getElement(), dialog.getTransactionID());
				}
			}
			return;
		}
		if (str.startsWith("trennen")) {
			int selrows = ltree.getSelectionCount();
			if (selrows > 0) {
				NodeContainer knot;
				for (int n = 0; n < selrows; n++) {
					if (((LGMTreeNode) ltree.getLastSelectedPathComponent()).getUserObject() instanceof String)
						return;
					knot = (NodeContainer) ((LGMTreeNode) ltree.getLastSelectedPathComponent()).getUserObject();
					doc.unlink(modelElement, knot.getElement(), dialog.getTransactionID());
				}
			}
			return;
		}
	}
	*/

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
