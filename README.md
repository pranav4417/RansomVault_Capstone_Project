# RansomVault [Capstone Project]

**RansomVault** is a Java-based secure encrypted vault system that creates a hidden, password-protected volume inside the same physical disk. Designed to protect sensitive data from ransomware attacks and unauthorized access—even by administrators—this project ensures your files remain safe by mounting and locking the vault on demand.

---

## Features

- Creates an encrypted container on the existing disk without external drives  
- Uses strong AES-256 encryption for data protection  
- Password-protected access to mount/unmount the vault  
- Vault remains hidden and inaccessible when unmounted  
- Java-based GUI for easy mounting, backup, and locking operations  
- Secure backup with hash verification to prevent data tampering  
- Minimizes exposure time to malware by automatic dismounting after use  

---

## Problem Statement

With the rise of ransomware and malware attacks, sensitive data on personal computers is increasingly vulnerable. Traditional security measures often fail when attackers gain admin privileges or perform full disk encryption. RansomVault_Capstone addresses this by providing a secure, hidden encrypted volume on the same disk that stays locked and inaccessible without proper authorization.

---
## Technologies Used
- Java (JDK 11+)
- Swing/AWT for GUI (or JavaFX if applicable)
- AES-256 Encryption (via libraries like Bouncy Castle or built-in crypto)
- File I/O and hashing for backup integrity

---
## Future Work
- Integrate automated vault mounting/dismounting based on system triggers
- Support for multi-factor authentication
- Expand to network drives or external storage options
- Enhance UI with advanced features and animations

---
## Authors
- Pranav Kandakurthi - Student at Presidency University(Reg# 20221CSE0404)
- Soujanya Ravikumar Nadig - Student at Presidency University(Reg# 20221CSE0370)
- Shubham Manu Pathak - Student at Presidency University(Reg# 20221CSE0389)



