import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileCSV {

    static String formattedDateTime = "BRAK DATY ODCZYTU";
    static String password = "OperationCanceled";
    public static void SetNoDateAndPass(){
        formattedDateTime = "BRAK DATY ODCZYTU";
        password = "OperationCanceled";
    }

    private static void SetDate()
    {
        //Zapisanie daty do pliku
        // Pobierz bieżącą datę i czas
        LocalDateTime now = LocalDateTime.now();

        // Formatowanie daty i czasu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formattedDateTime = now.format(formatter);
    }
    public static boolean loadDataFromFile(DefaultTableModel tableModel, String filePath, MyJFrame frame) {


            File file = new File(filePath);

            //if (tableModel.getRowCount() > 0) {
                tableModel.setRowCount(0);      // Usuń wszystkie wiersze
                tableModel.setColumnCount(0);   // Usuń wszystkie kolumny
            //}

            password = getPassword();
            if (password.equals("OperationCanceled"))
                return false;

            try (BufferedReader br = new BufferedReader(new FileReader(file)) ) {
                String line;

                // Odczytanie pierwszej linii (nagłówka) i ustawienie kolumn
            /*
            if ((line = br.readLine()) != null) {
                String[] columns = line.split(";");
                for (String column : columns) {
                    tableModel.addColumn(  CaesarCipher.Do(column,password, CaesarCipher.Operation.Decode) );
                }
            }
             */

                //Ustawienie na sztywno wszystkich kolumn
                TableOper.setColumns(tableModel);

                if ((line = br.readLine()) != null) {
                    formattedDateTime = line;
                }
                if (formattedDateTime.equals("BRAK DATY ODCZYTU")) {
                    JOptionPane.showMessageDialog(frame, "Baza haseł nie była jeszcze odszyfrowywana z pliku " + filePath,
                            "Ostatni odczyt z pliku " + filePath, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Baza haseł była ostatnio odszyfrowywana z pliku " + filePath + " :" + formattedDateTime,
                            "Ostatni odczyt z pliku " + filePath, JOptionPane.INFORMATION_MESSAGE);
                }

                // Read remaining lines and add rows to the table model
                while ((line = br.readLine()) != null) {
                    String[] rowData = line.split(";");
                    if (rowData.length != 5) {
                        String message = "Nieprawidłowa liczba kolumn w pliku " + filePath + ". Powinno być 5 kolumn a jest " + rowData.length;
                        JOptionPane.showMessageDialog(frame, message, "Błąd", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = CaesarCipher.Do(rowData[i], password, CaesarCipher.Operation.Decode);
                    }

                    tableModel.addRow(rowData);
                    SetDate();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        //saveTableDataToCSV(tableModel, filePath);
        return true;
    }

    public static String getPassword(){
        // Tworzymy JFrame jako rodzica dla JOptionPane
        JFrame frame = new JFrame("Podaj hasło");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Tworzymy JPasswordField
        JPasswordField passwordField = new JPasswordField(10);

        // Tworzymy panel i dodajemy do niego pole hasła
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Podaj hasło:"));
        panel.add(passwordField);

        // Wyświetlamy okno dialogowe z opcjami OK i Cancel
        int option = JOptionPane.showConfirmDialog(frame, panel, "Hasło", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            // Pobieramy hasło z JPasswordField
            char[] password = passwordField.getPassword();
            frame.dispose();
            return new String(password);
        } else {
            frame.dispose();
            return new String("OperationCanceled");
        }

    }
    public static void saveTableDataToCSV(DefaultTableModel tableModel, String filePath) {
        File file = new File(filePath);

        if (password.equals("OperationCanceled"))
            password = getPassword();

        if (password.equals("OperationCanceled"))
            return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            //DefaultTableModel model = (DefaultTableModel) table.getModel();

            // Zapisanie do pliku nazw kolumn
            /*
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                bw.write(  CaesarCipher.Do( tableModel.getColumnName(i).toString(),password, CaesarCipher.Operation.Encode) );
                if (i < tableModel.getColumnCount() - 1) {
                    bw.write(";");
                }
            }
            bw.newLine();
            */

            bw.write(formattedDateTime);

            bw.newLine();

            // Zapisanie do pliku wierszy
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    bw.write(  CaesarCipher.Do( tableModel.getValueAt(i, j).toString(),password, CaesarCipher.Operation.Encode)  );
                    if (j < tableModel.getColumnCount() - 1) {
                        bw.write(";");
                    }
                }
                bw.newLine();
            }

            bw.flush();
            bw.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
