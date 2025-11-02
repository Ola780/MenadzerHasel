import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MyJFrame extends JFrame implements ActionListener {


    private boolean otwartoPlik = false;
    private JMenuBar menuBar;
    private JMenu menuPlik, menuPomoc; //menuNarzedza,
    private JMenuItem mOtworz, mNowy, mZapisz,mZapiszJako,  mWyjscie, mOProgramie;
            //mWyszukaj, mSortuj, mDodajHaslo,
            //mUsunHaslo, mGenerujHasło, mDodajKat, mUsunKat;

    private JTable table;
    TableRowSorter<TableModel> sorter; //TableModel
    JScrollPane scrollPane;
    private DefaultTableModel tableModel;

    private String fileName = null;

    //Komponenty do filtrowania i edycji
    //private JTextField textFilter;     //Pole do filtrowania po kategorii
    private JTextField textNazwa,textHaslo, textKategoria, textLogin, textStrona;       //Pola do edycji wyszystkich danych hasła

    private JButton buttonFilter, buttonCatDelete, buttonAdd, buttonEdit, buttonDelete;
    private JPanel panelFilter, panelAddEditDelete;
    private JComboBox<String> comboCategory;


    public MyJFrame() {
        //super();
        setTitle("Menedżer haseł");
        setSize(1300, 500);


        SetMenu();      //Wstawienie menu  do ramki
        CreateTable();  //Stworzenie 3 paneli: filtrowania, scrollowania tabeli i edycji (panele są ukryte)

        //setLayout(new FlowLayout());
        //pack();

        setVisible(true);               //Pokazanie okna (ramka jest widoczna)
        setLocationRelativeTo(null);    //Wyśrodkowanie okna względem całego ekranu, jeżeli zamiast null
        //przekazalibyśmy jakiś component (obiekt) do wyśrodkowanie względem tego komponentu

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Przy zamknięciu okna ramka zostanie usunięta (może być np.: tylko ukryta)
        //obiekt JFrame jest niszczony i program kończy działanie.

    }

    //nasłuchiwanie zdarzeń ramki
    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource(); //zródłow zdarzenie będzie przechowywało obiekt

    }

    //Wyświetlenie menu
    private void SetMenu(){

        //Stworzenie obiektu pasek menu
        menuBar = new JMenuBar();
        setJMenuBar(menuBar); //nie add a set, dodanie manu do JFrame i odpowiednie ustawienie

        //Obkiety menu najwyższego poziomu (na pasku menu)
        menuPlik = new JMenu("Plik");
        menuPomoc = new JMenu("Pomoc");

        //dodanie ww. obietów do paska Menu (JMenuBar)
        menuBar.add(menuPlik);
        menuBar.add(menuPomoc);


        //Utworzenie obietków, które są opcjami w podmenu Plik
        mOtworz = new JMenuItem("Otwórz ...");
        mNowy = new JMenuItem("Nowy");
        mZapisz = new JMenuItem("Zapisz");
        mZapiszJako = new JMenuItem("Zapisz jako ...");
        mWyjscie = new JMenuItem("Wyjście");

        //Dodanie ww. obiektów do podmenu "Plik" (mamy konkretne funkcje w podmenu "Plik")
        menuPlik.add(mOtworz);
        menuPlik.add(mNowy);
        menuPlik.add(mZapisz);
        menuPlik.add(mZapiszJako);
        menuPlik.addSeparator();
        menuPlik.add(mWyjscie);

        mZapiszJako.setEnabled(false);
        mZapisz.setEnabled(false);

        //Utworzenie obietków, które są opcjami w podmenu "O programie"
        mOProgramie = new JMenuItem("O programie");

        //Dodanie ww. obiektów do podmenu "Pomoc" (mamy konkretne funkcje w podmenu "Pomoc")
        menuPomoc.add(mOProgramie);
        MyJFrame frame = this;
        mOProgramie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Program przechowuje hasła oraz związane z hasłami informacje, które zostaną udostępnione po wpisaniu właściwego hasła dostępu do danych.",
                        "O programie", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        //Obiekt JFrame (bo przekazaliśmy this) będzie nasłuchiwał zdarzeń
        //i przekazywał obiekt wywołujący zdarzenie - ActionEvent e
        // do metody actionPerformed(ActionEvent e)
        mWyjscie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }); //this adres obietku, który ma metodę  actionPerformed(ActionEvent e)

        mOtworz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenFileToTable(); //Otwiera plik ładuje do tableModel i odkrywa 3 panele filtrowania, sortowania i edycji
            }
        });

        //Otwarcie nowego pliku
        mNowy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (otwartoPlik) {
                    otwartoPlik = false;
                    mZapisz.setEnabled(false);
                    mZapiszJako.setEnabled(false);
                }

                //Wyczyszczenie kolumn i wierszy
                if (tableModel.getRowCount() >0 ) {
                    tableModel.setRowCount(0);      // Usuń wszystkie wiersze
                    tableModel.setColumnCount(0);   // Usuń wszystkie kolumny
                }

                TableOper.setColumns(tableModel);
                comboCategory.addItem("<wszystkie>");

                ShowAll(); //pokazuje wszystkie panale
                TableOper.SetButtons(tableModel, buttonCatDelete, buttonEdit, buttonDelete);
            }
        });

        mZapisz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (otwartoPlik && fileName != null) //Jeżeli wcześniej otwarliśmy plik
                    FileCSV.saveTableDataToCSV(tableModel, fileName); //Zapisujem dane w otwartym wcześniej pliku
                else{ //Jeżeli wcześniej nie otwarliśmy pliku czyli zaczęliśmy od nowy
                    //Opcja taka jak zapisz jako
                    SaveFileFromTable();  //Pojawia się okienko do zapisu pliku z wybrany plik zapisujemy w polu fileName
                    FileCSV.SetNoDateAndPass(); //Mamy nowy plik więc data ostatniego odczytu jest ustawiana "BRAK DATY ODCZYTU"
                    FileCSV.saveTableDataToCSV(tableModel, fileName);
                }

            }
        });
        mZapiszJako.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveFileFromTable();
                FileCSV.saveTableDataToCSV(tableModel, fileName);
            }
        });

        /*Stworzenie osobnej klasy do obsługi actionPerformed(ActionEvent e)
        mija się z celem bo chcemy mieć wszystkie obiekty, których używa ramka w jednej klasie
        dziedziczącej po JFrame*/
    }




    //Ustawienia paneli oraz Listenera do każdego przycisku na każdym panelu
    private void SetEditFilterPanels() {


        comboCategory   = new JComboBox<String>();            // Tworzenie pola Combo Box do filtrowania
        buttonFilter    = new JButton("Filtruj");   // Tworzenie przycisku do filtrowania
        buttonCatDelete = new JButton("Usuń");      // Tworzenie przycisku do filtrowania
        panelFilter     = new JPanel();                 // Tworzenie panelu do filtrowania

        //Dodanie do panelu filtrowania przycisku, pola tekstowego i etykiery
        panelFilter.add(new JLabel("Wybierz kategorię:"));
        panelFilter.add(comboCategory);
        panelFilter.add(buttonFilter);
        panelFilter.add(buttonCatDelete);


        // Tworzenie przycisków do dodawania, usuwania i edytowania wierszy
        buttonAdd = new JButton("Dodaj");
        buttonDelete = new JButton("Usuń");
        buttonEdit = new JButton("Edytuj");


        // Tworzenie pól tekstowych do dodawania i edytowania danych
        textNazwa = new JTextField(10);
        textHaslo = new JTextField(10);
        textKategoria = new JTextField(10);
        textLogin = new JTextField(10);
        textStrona = new JTextField(20);


        // Tworzenie panelu do dodawania, usuwania i edytowania wierszy
        panelAddEditDelete = new JPanel();

        panelAddEditDelete.add(new JLabel("Nazwa:"));
        panelAddEditDelete.add(textNazwa);

        panelAddEditDelete.add(new JLabel("Hasło:"));
        panelAddEditDelete.add(textHaslo);

        panelAddEditDelete.add(new JLabel("Kategoria:"));
        panelAddEditDelete.add(textKategoria);

        panelAddEditDelete.add(new JLabel("Login:"));
        panelAddEditDelete.add(textLogin);

        panelAddEditDelete.add(new JLabel("Strona WWW:"));
        panelAddEditDelete.add(textStrona);

        panelAddEditDelete.add(buttonAdd);
        panelAddEditDelete.add(buttonDelete);
        panelAddEditDelete.add(buttonEdit);

        // Dodanie listenera do przycisku filtrowania
        buttonFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = comboCategory.getSelectedItem().toString();
                TableOper.filterTable(category, sorter, 2);  //Metoda JFrame do filtrowania tabeli
            }
        });

        // Dodanie listenera do przycisku usuawania kategorii usuwamy wszystkie wiersze z wybrana kategoria
        buttonCatDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category =  comboCategory.getSelectedItem().toString();

                if (!category.isEmpty()) {
                    TableOper.removeRowsByCategory(category, tableModel, 2); //Tu też z poziomu JFrame możemy usuwać kategorie
                }
                TableOper.SetCatComboValues(comboCategory,tableModel,2);    //po usunięciu wierszy zmieni się lista kategorii.
                TableOper.SetButtons(tableModel, buttonCatDelete, buttonEdit, buttonDelete);
            }
        });


        // Dodanie listenera do przycisku dodawania
        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                TableOper.addRow(comboCategory,tableModel,textNazwa.getText(), textHaslo.getText(),textKategoria.getText(),
                        textLogin.getText(),textStrona.getText());
                TableOper.SetButtons(tableModel, buttonCatDelete, buttonEdit, buttonDelete);

                mZapisz.setEnabled(true);
                mZapiszJako.setEnabled(true);
                //TableOper.SetCatComboValues(comboCategory,tableModel,2);    //po dodaniu wiersza może zmienić się lista kategorii.
            }
        });

        // Dodanie listenera do przycisku usuwania
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                }
                TableOper.SetCatComboValues(comboCategory, tableModel, 2);    //po skasowaniu wiersza może zmienić się lista kategorii.
                TableOper.SetButtons(tableModel, buttonCatDelete, buttonEdit, buttonDelete);

                if (tableModel.getRowCount() == 0) {
                    mZapisz.setEnabled(false);
                    mZapiszJako.setEnabled(false);
                }
            }
        });

        // Dodanie listenera do przycisku edytowania
        buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {

                    tableModel.setValueAt(textNazwa.getText(), selectedRow, 0);
                    tableModel.setValueAt(textHaslo.getText(), selectedRow, 1);
                    tableModel.setValueAt(textKategoria.getText(), selectedRow, 2);
                    tableModel.setValueAt(textLogin.getText(), selectedRow, 3);
                    tableModel.setValueAt(textStrona.getText(), selectedRow, 4);

                }
                TableOper.SetCatComboValues(comboCategory,tableModel,2);    //po edytowaniu wiersza może zmienić się lista kategorii.
            }

        });
    }



    private void CreateTable(){
        tableModel = new DefaultTableModel(); //Tworzymy nowy obiekt DefaultTableModel, który będzie modelem danych dla tabeli.
        table = new JTable(tableModel); //Tworzymy nową tabelę (JTable) z użyciem wcześniej utworzonego modelu tableModel.

        //table.setBounds(50,50,300,50);
        scrollPane = new JScrollPane(table);//Umieszczamy tabelę wewnątrz componentu JScrollPane, aby umożliwić przewijanie tabeli, jeśli będzie miała dużo danych
        //scrollPane.setBounds(50,50,300,50);

        // Dodanie możliwości sortowania i filtrowania tabeli
        sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter); //przypisujemy nasz sorter (sorter) do tabeli (table). Od tego momentu tabela będzie używać tego sortera do sortowania i filtrowania wierszy.
        //sorter.setRowFilter() w jakim wierszu bede filtrowac i w jak sposób;

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            //Metoda valueChanged z interfejsu ListSelectionListener jest wywoływana, gdy zmienia się wybór w liście lub tabeli.
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { //gdy zmiana wyboru w tabeli została zakończona
                    int selectedRow = table.getSelectedRow();   //pobranie zaznaczonego wiersza pobieram indeks zaznaczonego wiersza od 0
                    if (selectedRow != -1){          //tabela jest pusta
                        //Ustawienie pól tekstowych
                        textNazwa.setText(table.getValueAt(selectedRow, 0).toString());//zwraca wartosci pierwszej kolumny w wybrsnym wierszu
                        textHaslo.setText(table.getValueAt(selectedRow, 1).toString());
                        textKategoria.setText(table.getValueAt(selectedRow, 2).toString());
                        textLogin.setText(table.getValueAt(selectedRow, 3).toString());
                        textStrona.setText(table.getValueAt(selectedRow, 4).toString());
                    }
                }
            }
        });

        //Wstawienie paneli (filtrowanie, scroll z tabelą i Add Edit i Delete )
        // oraz Listenera do każdego przycisku na każdym panelu
        SetEditFilterPanels();
        panelAddEditDelete.setLayout(new BoxLayout(panelAddEditDelete,BoxLayout.X_AXIS )); //BoxLayout.Y_AXIS));
        //panelFilter.setLayout(new BoxLayout(panelFilter,BoxLayout.X_AXIS));
        // Dodanie paneli do ramki
        add(panelFilter, "North");
        add(scrollPane, "Center"); //Dodanie JScrollPane do JFrame
        add(panelAddEditDelete, "South");
        panelFilter.setVisible(false);
        scrollPane.setVisible(false);
        panelAddEditDelete.setVisible(false);

        //wypełnienie całej dostępnej przestrzeni widoku przez tabelę, nawet jeśli tabela nie zawiera wystarczającej liczby wierszy, aby ją wypełnić.
        // Jest to przydatne, gdy chcemy, aby tabela wyglądała estetycznie i zajmowała cały dostępny obszar w swoim kontenerze (w JScrollPane).
        //table.setFillsViewportHeight(true);


    }
    //Wybór pliku i załadoawnie pliku do tabeli
    private  void ShowAll(){
        //Pokazujemy wszystkie panele
        panelFilter.setVisible(true);
        scrollPane.setVisible(true);
        panelAddEditDelete.setVisible(true);

        sorter.setRowFilter(null);//Wyłączamy wcześniejsze filtrowanie


        setVisible(true); //Żeby tabela została wyświetlona,całą ramkę renderujemy
    }

    private void SaveFileFromTable() {

        //Obiekt JFileChooser służy do wyboru pliku
        JFileChooser fileChooser = new JFileChooser("Zapisz plik");
        //Wybieramy wyłącznie pliki CSV (plik tekstowy, a wartości rozdzielone średnikiem ";")
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv"); //stworzenie obkietku filtra
        fileChooser.setFileFilter(filter); //ustawienie filra dla obkiektu JFileChooser

        int returnValue = fileChooser.showSaveDialog(this); //otwiera okno dialogowe do zapisu pliku
        /*Metoda ta zwraca wartość typu int, która może być:
            JFileChooser.APPROVE_OPTION (jeśli użytkownik wybrał plik) lub
            JFileChooser.CANCEL_OPTION (jeśli użytkownik anulował operację).
         */
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile(); //zwraca obiekt File, który reprezentuje wybrany plik.
            fileName = selectedFile.getAbsolutePath(); //zwraca pełną ścieżkę do pliku jako string
            fileName += ".csv";
            otwartoPlik = true;
        }

    }
    private void OpenFileToTable() {

        //Obiekt JFileChooser służy do wyboru pliku
        JFileChooser fileChooser = new JFileChooser("");
        //Wybieramy wyłącznie pliki CSV (plik tekstowy, a wartości rozdzielone średnikiem ";")
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv"); //stworzenie obkietku filtra
        fileChooser.setFileFilter(filter); //ustawienie filra dla obkiektu JFileChooser

        int returnValue = fileChooser.showOpenDialog(this); //otwiera okno dialogowe do wyboru pliku
        /*Metoda ta zwraca wartość typu int, która może być:
            JFileChooser.APPROVE_OPTION (jeśli użytkownik wybrał plik) lub
            JFileChooser.CANCEL_OPTION (jeśli użytkownik anulował operację).
         */
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile(); //zwraca obiekt File, który reprezentuje wybrany plik.
            fileName = selectedFile.getAbsolutePath(); //zwraca pełną ścieżkę do pliku jako string

            otwartoPlik = true;
            //Załadowanie danych z pliku podanego jako argument
            //i zapisanie danych w polu tableModel (modelu danych tabeli)
            if (FileCSV.loadDataFromFile(tableModel, fileName, this)) {
                FileCSV.saveTableDataToCSV(tableModel, fileName);
                TableOper.SetCatComboValues(comboCategory, tableModel, 2); //Po załadowaniu danych ustawiamy wartości w ComboBox
                ShowAll();
                mZapisz.setEnabled(true);
                mZapiszJako.setEnabled(true);
            }
        }
    }






}
