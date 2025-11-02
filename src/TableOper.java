import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Stack;
import java.util.regex.Pattern;

public class TableOper {

    public static void setColumns(DefaultTableModel tableModel){
        String columns[] = {"Nazwa", "Hasło","Kategoria","Login","Strona WWW"};

        for (String column : columns) {
            tableModel.addColumn(column);
        }
        /*
        tableModel.addRow(new Object[]{"poczta1", "123","wazne", "alex","poczta02.pl"});
        tableModel.getValueAt(1,1).toString();
        tableModel.getRowCount();
        tableModel.getColumnCount();

        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        tableModel.removeRow(1);
        */
    }



    //Filtowanie tabeli po kategorii
    public static void filterTable(String category, TableRowSorter<TableModel> sorter, int column) {
        //if (category.trim().length() == 0) {
        if ( category.equals("<wszystkie>") ) {
            sorter.setRowFilter(null); //jeżeli pole tekstowe filtra jest puste, wszystkie wiersze są widoczne
        } else {
            // argumenty setRowFilter
            // a) Wyrażenie regularne, które będzie używane do filtrowania. W naszym przypadku jest to kategoria
            // b) Indeksy kolumn, do których ma być zastosowany filtr. W naszym przypadku jest to 2, co oznacza trzecią kolumnę (kolumna "Kategoria")
            sorter.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(category) + "$", column));
        }
    }

    public static void SetButtons(DefaultTableModel tableModel, JButton buttonCatDelete, JButton  buttonEdit, JButton  buttonDelete) {
        if ( tableModel.getRowCount() == 0){
            buttonCatDelete.setEnabled(false);
            buttonEdit.setEnabled(false);
            buttonDelete.setEnabled(false);
        } else {
            buttonCatDelete.setEnabled(true);
            buttonEdit.setEnabled(true);
            buttonDelete.setEnabled(true);
        }

    }

    //Usuwanie wierszy po kategorii
    public static void removeRowsByCategory(String category, DefaultTableModel tableModel, int column) {

        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) { //Idziemy po wszystkich wierszach z modelu Tabeli (dane tabeli)
            //Jeżeli wartość w i-tym wierszu i 2 kolumnie jest równa kategorii
            String currentCategory = tableModel.getValueAt(i, column).toString();
            if ( currentCategory.equals(category) || category.equals("<wszystkie>") ) { //Kategoria jest w kolumnie 2
                tableModel.removeRow(i); //usuń i-ty wiersz
            }
        }
    }

    //Mamy już dane w modelu, czyli uruchomiono  już loadDataFromFile(selectedFile.getAbsolutePath())
    // i wpisujemy je do comboBox
    public static void SetCatComboValues(JComboBox<String> comboCategory,DefaultTableModel tableModel,int column ) {
        if (comboCategory.getItemCount() > 0 )
            comboCategory.removeAllItems();

        comboCategory.addItem("<wszystkie>");
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) { //Idziemy po wszystkich wierszach z modelu Tabeli (dane tabeli)
            String category = tableModel.getValueAt(i, column).toString(); //Kategoria jest w kolumnie 2

            if (!categoryExists(comboCategory,category)) {
                comboCategory.addItem(category);
            }
        }
    }
    public static void addRow(JComboBox<String> comboCategory,DefaultTableModel tableModel, String name, String pass, String category, String login, String www) {
        tableModel.addRow(new Object[]{name, pass, category, login, www});
        if (!categoryExists(comboCategory, category)) {
            comboCategory.addItem(category);  // Dodanie nowej kategorii do JComboBox
        }
    }

    public static boolean categoryExists(JComboBox<String> comboCategory, String category) {
        for (int i = 0; i < comboCategory.getItemCount(); i++) {
            if (comboCategory.getItemAt(i).equals(category)) {
                return true;
            }
        }
        return false;
    }
}
