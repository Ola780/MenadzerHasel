# Password Manager (Java Swing)

Desktop password manager application written in Java using Swing.

The application stores passwords together with their sources in encrypted files. After launching the program, the user can choose an existing password file or create a new one. Access to the file is protected by a master password used for encryption and decryption.

Regardless of whether the entered master password is correct or not, the application allows working with the file — incorrect passwords result in unreadable data, which reflects the encryption mechanism.

## Features
- Desktop GUI built with Java Swing
- File-based password storage (CSV)
- Encryption and decryption using a master password
- Create new password files or open existing ones
- Password management:
  - add, edit, delete entries
  - filter and sort passwords by category
- Category-based filtering
- Data persistence to encrypted files

## Technologies
- Java
- Swing (GUI)
- File I/O
- Basic encryption logic

## Notes
This project focuses on desktop application development, GUI design, and file-based data encryption rather than production-grade security.
