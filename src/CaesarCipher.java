public class CaesarCipher {
    enum Operation {Encode, Decode};

    //Funkcja obliczająca przesunięcie na podstawie hasła.
    // Sumuje wartości ASCII znaków hasła i oblicza resztę z dzielenia przez 26 (liczba liter w alfabecie łacińskim + x + y).
    public static int calculateShift(String password) {
        int shift = 0;
        for (char c : password.toCharArray()) {
            shift += c;
        }
        return shift % 26;
    }

    //Za każdym razem przy zapisie i odczycie pliku należy podać hasło
    public static String Do(String text,String password, Operation oper) {

        int shift = calculateShift(password);
        if (oper == Operation.Decode)
            shift = 26-shift;

        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) { //Sprawdzenie, czy znak c jest literą. Szyfr Cezara działa tylko na litery alfabetu łacińskiego (A-Z, a-z), dlatego musimy sprawdzić, czy c jest literą
                // Ustalenie "bazy" (punktu początkowego) dla przesunięcia, w zależności od tego, czy litera jest wielka (uppercase) czy mała (lowercase)
                //dla wielkiej litery baza to kod znaku 'A', dla małej litery baza to kod znaku 'a'
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                //c-base -- to indeks litery w alfabecie
                result.append((char) ((c - base + shift) % 26 + base));
            } else {
                result.append(c);//jeżeli nie litera to jako zwyły znak bez kodowania
            }
        }

        return result.toString();
    }


}
