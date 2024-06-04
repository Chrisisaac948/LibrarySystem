
//Package Declaration and Imports
package vu.booklibrary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

//Class Declaration and Fields
public class LibraryManager extends JFrame {
    private JTextField titleField, authorField, yearField;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    
    //Constructor
    public LibraryManager() {
        setTitle("Library Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        //Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);
        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        formPanel.add(yearField);

        
        //Buttons
        // Add, Delete, and Refresh buttons 
        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton refreshButton = new JButton("Refresh");
        formPanel.add(addButton);
        formPanel.add(deleteButton);
        formPanel.add(refreshButton);

        add(formPanel, BorderLayout.NORTH);

        
        //Table Initialization
        // Table to display books
        tableModel = new DefaultTableModel(new String[]{"BookID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        
        
        
        // Action listeners for buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();

                //Initial Table Load
                refreshTable();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
                //Initial Table Load
                refreshTable();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        // Initial table load
        refreshTable();
    }

    
    //Method: addBook()
    private void addBook() {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO Books (Title, Author, Year) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titleField.getText());
            pstmt.setString(2, authorField.getText());
            pstmt.setInt(3, Integer.parseInt(yearField.getText()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    //Method: deleteBook()
    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int bookID = (int) tableModel.getValueAt(selectedRow, 0);
            try (Connection conn = getConnection()) {
                String sql = "DELETE FROM Books WHERE BookID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, bookID);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    //Method: refreshTable()
    private void refreshTable() {
        tableModel.setRowCount(0);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("BookID");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                int year = rs.getInt("Year");
                tableModel.addRow(new Object[]{id, title, author, year});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    //Method: getConnection() to database
    private Connection getConnection() throws SQLException {
        String url = "jdbc:ucanaccess://C:/Users/ISAAC/Documents/NetBeansProjects/BookLibrary/Library.accdb";
        return DriverManager.getConnection(url);
    }

    
    //Main Method to display panel
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibraryManager().setVisible(true);
            }
        });
    }
}
