/*
 * Created on 25.10.2007
 *
 */
package de.imise.tool3lgm.graphtools.dialog.dragdrop;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.EventObject;

import javax.swing.DropMode;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;


/**
 * @author fstephan
 * 
 * Diese Klasse stellt statische Methoden bereit, um in einem <code>LGMDragNDropTree</code>
 * die DragNDrop-Funktion zu integrieren.
 * Außerdem besteht die Möglichkeit eine Aktionsfolge für das DragNDrop zwischen Trees
 * zu erstellen, die keinen gemeinsamen Aktionsbutton besitzen, sondern über weitere Trees 
 * und dazugehörige ActionButtons verbunden sind.
 */
public class DragNDropInitializer {
	
	public static boolean isExecuting=false; 
	
	/**
	 * Diese Methode initialisiert die DragNDrop Funktion vom <code>srcTree</code> zum 
	 * <code>targetTree</code> der übergebenen <code>DragNDropActionChain</code>.
	 * 
	 * Beim Ausführen einer DragNDrop-Aktion vom <code>srcTree</code> zum 
	 * <code>targetTree</code> wird die in der <code>DragNDropActionChain</code> 
	 * enthaltenen Folge von <code>LGMAction</code>s nacheinander ausgeführt.
	 * 
	 * @param dndAC
	 */
	public static void initDragNDrop(DragNDropActionChain dndAC) {
		// Das(Die) zu droppende(n) Element(e), ist(sind) auch das(die) markierte(n) Element(e)
		dndAC.getTargetTree().setDropMode(DropMode.USE_SELECTION);
		
		// Dragging für den srcTree aktivieren
		dndAC.getSrcTree().setDragEnabled(true);
		
		// Für den srcTree, wird der targetTree als DropTarget gesetzt 
		dndAC.getTargetTree().setDropTarget(getObjectAsDropTarget(dndAC));
		
	}

	
	/**
	 * Diese Methode initialisiert die DragNDrop Funktion vom <code>srcTree</code> zum 
	 * <code>targetTree</code>.
	 * 
	 * Beim Ausführen einer DragNDrop-Aktion vom <code>srcTree</code> zum 
	 * <code>targetTree</code> wird <code>action</code> ausgeführt.
	 * 
	 * @param srcTree
	 * @param targetTree
	 * @param action
	 */
	public static void initDragNDrop(LGMDragNDropTree srcTree, LGMDragNDropTree targetTree, LGMAction action) {
		
		DragNDropActionChain dndAC = createNewDragNDropActionChain(srcTree,targetTree,action);
		
		initDragNDrop(dndAC);
	}
	
	
	/**
	 * Diese Methode liefert den targetTree als DropTarget für den srcTree
	 * 
	 * @param srcTree
	 * @param targetTree
	 * @param actionButton
	 */
	private static DropTarget getObjectAsDropTarget(DragNDropActionChain dndAC) {
		
		DropTarget dt = new DropTarget(dndAC.getTargetTree(),TransferHandler.MOVE, getNewDropTargetAdapter(dndAC));
		
		return dt;
	}
	
	
	/**
	 * Methode erzeugt eine Instanz von <code>DropTargetAdapter</code>.
	 * Mittels dieses <code>DropTargetAdapter</code>s wird beim Eintreten eines
	 * DragNDrop-Ereignisses, die in <code>dragNDropActionChain</code> enthaltene
	 * Aktionsfolge ausgeführt. 
	 * 
	 * Außerdem werden die verschobenen Elemente im targetTree automatisch wieder
	 * selektiert. 
	 * 
	 * @param dragNDropActionChain
	 */
	private static DropTargetAdapter getNewDropTargetAdapter(DragNDropActionChain dragNDropActionChain) {
		
		final DragNDropActionChain dndAC = dragNDropActionChain;
		
		DropTargetAdapter dta = new DropTargetAdapter() {
			
			/**
			 * Methode wird beim Eintreten eines DragNDrop-Ereignisses aufgerufen.
			 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
			 */
			@Override
			public void drop(DropTargetDropEvent dtde) {
				
				if(dndAC.isValid() == false) return;
								
				while(dndAC.isEmpty() == false) {
					
					DragNDropActionChain.DragNDropAction dndA = dndAC.getNextDragNDropAction();
					
					if(dndA.isValid() == false) return;
					
					LGMDragNDropTree dndSrcTree = dndA.getSrcTree();
					LGMDragNDropTree dndTargetTree = dndA.getTargetTree();
					LGMAction action = dndA.getAction();
					
					// Selektierten Node ermitteln
					TreePath[] selectedPaths = dndSrcTree.getSelectionPaths();
										
					// ActionEvent ausführen
					if (executeAction(action,dtde) == true) { // Ausführung erfolgreich
						dtde.dropComplete(true);
						
						/*
						 * bei DragNDrop Verkettung, wird das srcElement, das
						 * in den targetTree verschoben wird, dort markiert
						 * um danach in einen weiteren Tree verschoben werden zu können
						 */ 
						dndTargetTree.addSelectionPaths(selectedPaths);
						
					}
					else { // Ausführung nicht erfolgreich
						dtde.rejectDrop();
						dtde.dropComplete(false);
					}
					
					dndSrcTree.updateUI();
					dndTargetTree.updateUI();
				}
				dndAC.restoreActionCount();
			}
			
			/**
			 * Methode sorgt für das Ausführen von <code>action</code>.
			 * Methode gibt wieder, ob die Aktion erfolgreich ausgeführt wurde.
			 * 
			 * @param al
			 * @param ae
			 */
			private boolean executeAction(LGMAction action,DropTargetDropEvent dtde) {
				
				try {
					action.execute(dtde);
					return true;
				}
				catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}			
		};
		
		return dta;
	}
	
	
	/**
	 * Diese Methode erstellt anhand der übergebenen Parameter eine neue 
	 * <code>DragNDropActionChain</code> und gibt diese wieder.
	 * 
	 * @see DragNDropActionChain
	 * 
	 * @param trees
	 * @param actions
	 */
	public static DragNDropActionChain createNewDragNDropActionChain(LGMDragNDropTree[] trees,LGMAction[] actions) {
		return new DragNDropActionChain(trees, actions);
	}
	
	
	/**
	 * Diese Methode erstellt anhand der übergebenen Parameter eine neue 
	 * <code>DragNDropActionChain</code> und gibt diese wieder.
	 * 
	 * @see #createNewDragNDropActionChain(LGMDragNDropTree[] trees,LGMAction[] actions)
	 * mit <code>trees</code> = {<code>srcTree</code>,<code>targetTree</code>} und 
	 * <code>actions</code> = {<code>action</code>}.
	 * 
	 * @param srcTree
	 * @param targetTree
	 * @param action
	 */
	public static DragNDropActionChain createNewDragNDropActionChain(LGMDragNDropTree srcTree, LGMDragNDropTree targetTree,LGMAction action) {
		
		LGMDragNDropTree[] trees = new LGMDragNDropTree[]{srcTree,targetTree};
		LGMAction[] actions = new LGMAction[]{action};
		
		return createNewDragNDropActionChain(trees,actions);
		
	}
		

	/**
	 * @author fstephan
	 * 
	 * Diese Klasse bietet die Möglichkeit eine Aktionsfolge für das DragNDrop 
	 * zwischen Trees zu erstellen.
	 * 
	 * Mittels der Aktionsfolgen kann so auch DragNDrop zwischen Trees erfolgen, die 
	 * keinen gemeinsamen Aktionsbutton besitzen sondern über weitere Trees und 
	 * dazugehörige ActionButtons verbunden sind.
	 * 
	 */
	public static class DragNDropActionChain {
		
		/**
		 * Reihenfolge der zu durchlaufenden Trees
		 */
		private LGMDragNDropTree[] trees;
		
		/**
		 * auszuführende Aktionsfolge
		 */
		private LGMAction[] actions;
		
		/**
		 * Anzahl der bereits ausgeführten Aktionen
		 */
		private int actionCount;
		
		
		/** ************************************************************************* */
		
		/**
		 * Konstruktor
		 * 
		 * 
		 * Syntax:
		 * <code>trees</code> = [srcTree, tree 2, tree 3, ... , tree n-1, targetTree]
		 * <code>actions</code> = [action 1, action 2, ..., action n-1]
		 * --> <code>trees.length - 1</code> = <code>actions.length</code>
		 * 
		 * Semantik:
		 * Bei DragNDrop von srcTree zu targetTree führe zwischen tree i und tree i+1 action i 
		 * für alle i aus.
		 * 
		 * 
		 * @param trees
		 * @param actions
		 */
		private DragNDropActionChain(LGMDragNDropTree[] trees, LGMAction[] actions) {
			
			this.trees = trees;
			this.actions = actions;
			this.actionCount = 0;
		}
		
		/**
		 * Methode überprüft ob diese Instanz eine gültige <code>DragNDropActionChain</code>
		 * ist.
		 */
		private boolean isValid() {
			return (trees.length == actions.length + 1);
		}
		
		/**
		 * Methode gibt zurück, ob bereits alle <code>actions</code> abgearbeitet
		 * worden sind.
		 */
		private boolean isEmpty() {
			return !(this.actionCount < this.actions.length);
		}
		
		/**
		 * Methode gibt anhand des internen <code>actionCount</code>s die nächste 
		 * auszuführende <code>DragNDropAction</code> wieder.
		 * Diese <code>DragNDropAction</code> muss beim eintreten des dazugehörigen
		 * dropping-Ereignisses ausgeführt werden.
		 */
		private DragNDropAction getNextDragNDropAction() {
			
			DragNDropAction dndA = new DragNDropAction(this.trees[actionCount], this.trees[actionCount+1],this.actions[actionCount]);
			actionCount++;
			
			return dndA;
		}
		
		/**
		 * Methode gibt den targetTree der Aktionsfolge wieder
		 */
		public LGMDragNDropTree getTargetTree() {
			return trees[trees.length-1];
		}
		
		/**
		 * Methode gibt den srcTree der Aktionsfolge wieder
		 */
		public LGMDragNDropTree getSrcTree() {
			return trees[0];
		}
		
		/**
		 * Methode setzt den <code>actionCount</code> auf 0 zurück.
		 * Kann nicht während der Abarbeitung der <code>DragNDropActionChain</code>
		 * ausgeführt werden.
		 */
		private void restoreActionCount() {
			if (this.isEmpty()) this.actionCount = 0;
		}
		
		/**
		 * @return <code>trees</code>
		 */
		public LGMDragNDropTree[] getTrees() {
			return this.trees;
		}
		
		/**
		 * @return <code>actions</code>
		 */
		public LGMAction[] getActions() {
			return this.actions;
		}
				
		
	    /**
	     * @author fstephan
	     *
	     * Diese Klasse stellt genau eine atomare Aktion dar, die bei einem dropping-Ereignis
	     * ausgelöst wird.
	     * 
	     * Instanzen dieser Klasse sind ausführbare Aktionen, die mit
	     * <code>execute(EventObject)</code> oder <code>actionPerformed(ActionEvent e)</code>
	     * ausgeführt werden können.
	     */
		private class DragNDropAction extends LGMAction{
			
			/**
			 * beinhaltet die zu verschiebenden Elemente
			 */
			private LGMDragNDropTree srcTree;
			
			/**
			 * stellt die DropLocation dar
			 */
			private LGMDragNDropTree targetTree;
			
			/**
			 * auszuführende Aktion
			 */
			private LGMAction action;
			
			/**
			 * Konstruktor
			 * 
			 * @param srcTree
			 * @param targetTree
			 * @param action
			 */
			public DragNDropAction(LGMDragNDropTree srcTree, LGMDragNDropTree targetTree, LGMAction action) {
				
				super();
				this.srcTree = srcTree;
				this.targetTree = targetTree;
				this.action = action;			
			}
			
			/**
			 * Methode überprüft ob diese Instanz eine gültige <code>DragNDropAction</code>
			 * ist.
			 * 
			 * @return <code>true</code>, falls gültige Instanz, <code>false</code>, sonst
			 */
			public boolean isValid() {
				
				return (   this.srcTree != null 
						&& this.targetTree != null 
						&& this.action != null);
			}
			
			/**
			 * @return <code>srcTree</code>
			 */
			public LGMDragNDropTree getSrcTree() {
				return this.srcTree;
			}
			
			/**
			 * @return <code>targetTree</code>
			 */
			public LGMDragNDropTree getTargetTree() {
				return this.targetTree;
			}
			
			/**
			 * @return <code>action</code>
			 */
			public LGMAction getAction() {
				return this.action;
			}
			
			/**
			 * Methode führt <code>action</code> aus
			 * @see de.imise.tool3lgm.graphtools.dialog.action.LGMAction#execute(java.util.EventObject)
			 */
			@Override
			public void execute(EventObject e) {
				this.action.execute(e);
			}
		}
	}	
}




