package org.mattiadr.MultiComparer;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.comparer.Comparer;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ResultsWindow extends JFrame {

	private record ComparisonResultTableModel(Comparer comparer, List<ComparisonResult> data) implements TableModel {

		private static final String[] COLUMNS = new String[] {"URL", "Status Code", "Length", "Content Matches"};

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMNS.length;
		}

		@Override
		public String getColumnName(int i) {
			return COLUMNS[i];
		}

		@Override
		public Class<?> getColumnClass(int i) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(int i, int i1) {
			return false;
		}

		@Override
		public Object getValueAt(int row, int column) {
			return switch (column) {
				case 0 -> data.get(row).path;
				case 1 -> data.get(row).code;
				case 2 -> data.get(row).length;
				case 3 -> data.get(row).responseMatches;
				default -> null;
			};
		}

		@Override
		public void setValueAt(Object o, int i, int i1) {

		}

		@Override
		public void addTableModelListener(TableModelListener tableModelListener) {

		}

		@Override
		public void removeTableModelListener(TableModelListener tableModelListener) {

		}

		public Color getCellColor(int row, int column) {
			return switch (column) {
				case 1 -> data.get(row).getCodeColor();
				case 2 -> data.get(row).getLengthColor();
				case 3 -> data.get(row).getResponseColor();
				default -> null;
			};
		}

		public void sendRowToComparator(int row) {
			comparer.sendToComparer(data.get(row).baselineResponse.toByteArray());
			comparer.sendToComparer(data.get(row).comparedResponse.toByteArray());
		}

	}

	private static class ColorTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			ComparisonResultTableModel model = (ComparisonResultTableModel) table.getModel();
			Color color = model.getCellColor(row, column);
			if (color != null && !isSelected) {
				label.setBackground(color);
				label.setForeground(Color.BLACK);
			}
			return label;
		}

	}

	public ResultsWindow(MontoyaApi api, List<ComparisonResult> comparisonResults) {
		super("Comparison Results");

		// init scroll pane
		JScrollPane scrollPane = new JScrollPane();
		ComparisonResultTableModel tableModel = new ComparisonResultTableModel(api.comparer(), comparisonResults);
		JTable resultsTable = new JTable(tableModel);

		// set column color renderer
		ColorTableCellRenderer cellRenderer = new ColorTableCellRenderer();
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(450);
		resultsTable.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
		resultsTable.getColumnModel().getColumn(2).setCellRenderer(cellRenderer);
		resultsTable.getColumnModel().getColumn(3).setCellRenderer(cellRenderer);

		resultsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				Point point = e.getPoint();
				int row = table.rowAtPoint(point);
				if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
					ComparisonResultTableModel model = (ComparisonResultTableModel) table.getModel();
					model.sendRowToComparator(row);
				}
			}
		});

		// add table to scroll pane
		scrollPane.setViewportView(resultsTable);

		add(scrollPane);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

}
