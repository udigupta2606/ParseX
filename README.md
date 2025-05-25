# ParseX: Parsing Context-Free Grammars the CLR(1) Way

ParseX is an interactive **Java-based LR parser simulator** that supports the full family of bottom-up parsers: **LR(0)**, **SLR(1)**, **CLR(1)**, and **LALR(1)**. Designed for educational clarity, this tool allows users to input custom grammars, visualize parsing tables, and simulate the parsing process step-by-step via a modern JavaFX interface.

---

## Key Features

✅ Supports four parser types: LR(0), SLR(1), CLR(1), LALR(1)  
✅ Computes **FIRST** and **FOLLOW** sets  
✅ Builds canonical item collections and parsing tables  
✅ Detects **shift-reduce** and **reduce-reduce** conflicts  
✅ Visualizes **stack-based parsing** in real-time  
✅ Clean, user-friendly **JavaFX GUI**  
✅ Modular, well-structured Java code for easy extension  

---

## UI Preview

![image](https://github.com/user-attachments/assets/297ab8a5-4676-4d0a-9132-754a05eb0427)

![image](https://github.com/user-attachments/assets/f2be9b9f-8bd6-4bf4-8bed-dbdcda22e2bb)

![image](https://github.com/user-attachments/assets/cc98b536-f02d-4cbc-8131-c2689f309dd5)

- Grammar input screen  
- Parsing table display  
- Step-by-step parsing simulation view

---

## Project Structure

ParseX/
├── gui/ # JavaFX GUI and controllers

├── lr0/ # LR(0) and SLR(1) parsing logic

├── lr1/ # CLR(1) and LALR(1) parsing logic

├── util/ # Grammar parsing, FIRST/FOLLOW, data models

├── out/ # Output directory after compilation

---

## How to Compile and Run (Windows)

### Requirements

- Java JDK 17 or above  
- JavaFX SDK 17.0.15 or above compatible with Java JDK
  → [Download JavaFX](https://gluonhq.com/products/javafx/)  
  → Extract to: `C:\javafx-sdk-17.0.15\`

### One-Line Build & Run Command

Open **Command Prompt** in the project root folder and run:

javac --module-path "C:\javafx-sdk-17.0.15\lib" --add-modules javafx.controls,javafx.fxml -d out gui\*.java lr0\*.java lr1\*.java util\*.java && xcopy /s /i gui\*.fxml out\gui\ && java --module-path "C:\javafx-sdk-17.0.15\lib" --add-modules javafx.controls,javafx.fxml -cp out gui.Main

This command will:
Compile all .java files into out/
Copy .fxml files into out/gui/
Launch the application via gui.Main

## Sample Usage
Enter grammar rules in the GUI (e.g., S → E, E → E + T | T, T → i | ( E ))
Select the parser type (LR(0), SLR(1), etc.)
View:
FIRST and FOLLOW sets
Canonical item sets
ACTION and GOTO tables
Simulate parsing with an input string (e.g., i + i), and follow the step-by-step actions (shift, reduce, accept)

## Development Team
Team Name: CodeCore
Team ID: CD-VI-T039

Gurpreet Singh - Grammar utilities, FIRST/FOLLOW logic - gurpreetsinghsappal17@gmail.com
Udi Gupta - Parser engine (LR, SLR, CLR, LALR), logic, stack based visualization - udigupta2606@gmail.com
Suhawni Arora - JavaFX GUI, UI simulation, testing - arorasuhawni2302@gmail.com

## Learning Goals
This project helps students and professionals:
Understand the core mechanics of bottom-up LR parsing
Explore conflict detection and resolution strategies
Visualize parsing table generation
Experiment with ambiguous and recursive grammars
See how compilers perform syntax analysis

## Challenges Faced
Handling grammar ambiguity and conflict detection
Managing lookahead propagation in CLR(1) and LALR(1)
Designing smooth UI integration with backend logic
Simulating accurate parser stack transitions in all parser types
Merging independently developed modules via version control

## Future Enhancements
Add Generalized LR (GLR) parsing for ambiguous grammars
Implement semantic analysis and symbol tables
Export parsing tables and trace logs as PDF/CSV
Add animated parsing steps for better UX
Provide preloaded example grammars for beginners
Build a REST API for integration into web-based IDEs
Add grammar error suggestions and validations

## Academic Context
This project was developed as part of the Compiler Design (TCS-601) course at Graphic Era University, fulfilling all academic requirements and offering a robust, educational tool for understanding LR parsing.

## GitHub Repository
🔗 https://github.com/udigupta2606/ParseX

## License
This project is open for educational and academic use. Please credit the authors if you reuse or adapt any part of this codebase.
