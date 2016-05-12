//+======================================================================
// $Source:  $
//
// Project:   Tango
//
// Description:  Basic Dialog Class to display info
//
// $Author: pascal_verdier $
//
// Copyright (C) :      2004,2005,2006,2007,2008,2009,2009,2010,2011,2012,2013,2014,2015,2016
//						European Synchrotron Radiation Facility
//                      BP 220, Grenoble 38043
//                      FRANCE
//
// This file is part of Tango.
//
// Tango is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Tango is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Tango.  If not, see <http://www.gnu.org/licenses/>.
//
// $Revision:  $
//
// $Log:  $
//
//-======================================================================

package org.tango.hdbcpp.configurator.strategy;

import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import org.tango.hdbcpp.common.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


//===============================================================
/**
 *	JDialog Class to display info
 *
 *	@author  Pascal Verdier
 */
//===============================================================


@SuppressWarnings("MagicConstant")
public class StrategyPanel extends JPanel {

	private List<Strategy> strategyList;
	private int[] columnWidth = { 100, 50 };
	private static final String[] columnNames = { "Strategy", "Use It" };
	private static final int hPadding = 30;
	private static final int NAME = 0;
	//private static final int USED = 1;
	private static final String howTo = "\nSelect strategies to be used for HDB storage";
	//===============================================================
	/**
	 *	Creates new form StrategyDialog
	 */
	//===============================================================
	public StrategyPanel(List<Strategy> strategyList) {
		this.strategyList = strategyList;
		setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		JLabel label = new JLabel("HDB Strategy Selection");
		label.setFont(new Font("Dialog", Font.BOLD, 14));
		panel.add(label);
		add(panel, BorderLayout.NORTH);
		buildTable();
	}
	//===============================================================
	//===============================================================
	private void buildTable() {
		StrategyTableModel model = new StrategyTableModel();
		//noinspection NullableProblems
		JTable table = new JTable(model) {
			public String getToolTipText(MouseEvent event) {
				return manageTooltip(event);
			}
		};
		table.setDefaultRenderer(String.class, new LabelCellRenderer());
		table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 14));
		table.getTableHeader().setToolTipText(Utils.buildTooltip(howTo));
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				tableActionPerformed(evt);
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		//	Check name column width
		int nameWidth = getNameColumnWidth(table);
		if (nameWidth>columnWidth[NAME])
			columnWidth[NAME] = nameWidth;

		//  Set column width
		final Enumeration columnEnum = table.getColumnModel().getColumns();
		int i = 0;
		int width = 0;
		TableColumn tableColumn;
		while (columnEnum.hasMoreElements()) {
			width += columnWidth[i];
			tableColumn = (TableColumn) columnEnum.nextElement();
			tableColumn.setPreferredWidth(columnWidth[i++]);
		}
		scrollPane.setPreferredSize(new Dimension(width, 40+strategyList.size()*16));
	}
	//===============================================================
	//===============================================================
	private String manageTooltip(MouseEvent event) {
		JTable table = (JTable) event.getSource();
		String tip = null;
		if (isVisible()) {
			Point p = event.getPoint();
			switch (table.columnAtPoint(p)) {
				case NAME:
					Strategy strategy = strategyList.get(table.rowAtPoint(p));
					tip = Utils.buildTooltip(strategy.getHtmlDescription());
					break;
				default:
					tip = Utils.buildTooltip(howTo);
					break;
			}
		}
		return tip;
	}
	//===============================================================
	//===============================================================
	public List<Strategy> getStrategyList() {
		return strategyList;
	}
	//===============================================================
	//===============================================================
	private void tableActionPerformed(MouseEvent event) {
		JTable table = (JTable) event.getSource();
		Point clickedPoint = new Point(event.getX(), event.getY());
		int selectedRow = table.rowAtPoint(clickedPoint);
		int column = table.columnAtPoint(clickedPoint);
		int mask = event.getModifiers();
		//  Check button clicked
		if ((mask & MouseEvent.BUTTON1_MASK)!=0) {
			if (column>0) {
				Strategy strategy = strategyList.get(selectedRow);
				strategy.toggleUsed();
				repaint();
			}
		}
	}
	//===============================================================
	//===============================================================
	private int getNameColumnWidth(JTable table ) {
		List<String> names = new ArrayList<>();
		for (Strategy strategy : strategyList)
			names.add(strategy.getName());
		return getColumnWidth(table, names)+hPadding; // padding to be nice
	}
	//===============================================================
	/**
	 * @param table JTable where it will be displayed
	 * @param lines rows text list
	 * @return the specified text width to be displayed.
	 */
	//===============================================================
	public static int getColumnWidth(JTable table, List<String> lines) {
		return Utils.getLongestLine(lines).length() * 7;
		/** getGraphics returns  null
		Font font = table.getFont();
		Graphics graphics = table.getGraphics();
		FontMetrics metrics = graphics.getFontMetrics(font);
		return metrics.stringWidth(getLongestLine(lines));
		*/
	}
	//===============================================================
	//===============================================================




	//===============================================================
	/**
	* @param args the command line arguments
	*/
	//===============================================================
	public static void main(String args[]) {
		List<Strategy>	list = new ArrayList<>();
		list.add(new Strategy("Shutdown", true,
				"Accelerator is shutdown\n" +
				"A majority of equipments are down\nand attributes must not be stored in HDB"));
		list.add(new Strategy("USM", true,
				"User mode\nAccelerator is running for users."));
		list.add(new Strategy("MDT", true,
				"Machine Dedicated Day\nAccelerator is used for tests or short maintenance"));
		try {
			StrategyDialog dialog = new StrategyDialog(null, list);
			if (dialog.showDialog()==JOptionPane.OK_OPTION) {
				List<Strategy> strategyList = dialog.getStrategyList();
				for (Strategy strategy : strategyList)
					System.out.println(strategy);
			}
		}
		catch(DevFailed e) {
            ErrorPane.showErrorMessage(new Frame(), null, e);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	//=========================================================================
	//=========================================================================






	//=========================================================================
	/**
	 * The Table model
	 */
	//=========================================================================
	public class StrategyTableModel extends DefaultTableModel {
		//==========================================================
		public int getColumnCount() {
			return columnNames.length;
		}

		//==========================================================
		public int getRowCount() {
			return strategyList.size();
		}

		//==========================================================
		public String getColumnName(int columnIndex) {
			if (columnIndex>=getColumnCount())
				return columnNames[getColumnCount() - 1];
			else
				return columnNames[columnIndex];
		}
		//==========================================================
		public Object getValueAt(int row, int column) {
			if (column==0)
				return strategyList.get(row).getName(); // Row title
			return strategyList.get(row).isUsed();
		}
		//==========================================================
		/**
		 * @param column the specified co;umn number
		 * @return the cell class at first row for specified column.
		 */
		//==========================================================
		public Class getColumnClass(int column) {
			if (isVisible()) {
				if (column==0)
					return String.class;
				else
					return Boolean.class;
			} else
				return null;
		}
		//==========================================================
		//==========================================================
	}


	//=========================================================================
	/**
	 * Renderer to set cell color
	 */
	//=========================================================================
	public class LabelCellRenderer extends JLabel implements TableCellRenderer {

		//==========================================================
		public LabelCellRenderer() {
			setFont(new Font("Dialog", Font.BOLD, 12));
			setOpaque(true);
		}
		//==========================================================
		public Component getTableCellRendererComponent(
				JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText(strategyList.get(row).getName());
			return this;
		}
		//==========================================================
	}
	//=========================================================================
	//=========================================================================
}
