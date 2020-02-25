## HAL-examples

This is my open-source code repository of examples in the "[Hybrid Automata Library](http://halloworld.org/)" framework.

## Before you start
All examples use the open-source agent-based modeling platform called [HAL](http://halloworld.org/). In order to run this code base, you'll need to download the latest version of [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html) and an editor (we suggest using [IntelliJ Idea](https://www.jetbrains.com/idea/download/)).

### Setting up the project in IntelliJ Idea

1. Download or clone HAL.
2. Open Intellij Idea
(a) click "Import Project" from the welcome window. (If the main editor window opens, Navigate to the File menu and click New -> "Project from Existing Sources")
(b) Navigate to the directory with the unzipped HAL Source code ("Hal-master"). Click "Open." Inside this folder will be the following folders: Examples, LEARN_HERE, HAL, Testing, and the manual.pdf.
3. Intellij will now ask a series of questions/prompts. The first prompt will be "Import Project," and you will select the bubble that indicates "Create project from existing sources" and then click "Next."
4. The next prompt is to indicate which directory contains the existing sources. Navigate to the HAL-master folder and leave the project name as "HAL-master." Click Next.
5. Intellij may alert you that it has found several source files automatically. Leave the box checked and click Next.
6. Intellij should have imported two Libraries: 1) lib and 2) HalColorSchemes. If these are not found, you"ll need complete the optional step 10 after setup is complete.
7. Intellij will prompt you to review the suggested module structure. This should state the path to the "HAL- master" directory. Click next.
8. Intellij will ask you to select the Java JDK. Click the "+" and add the following files:
(a) Mac: navigate to "/Library/ Java/ JavaVirtualMachines/" (b) Windows: navigate to "C:\ Program Files\ Java\"
(c) Choose a JDK version 1.8 or later
9. Intellij will state "No frameworks detected." Click Finish.
10. If step 6 failed, you will need to do one more step and add libraries for 2D and 3D OpenGL visualization:
(a) Navigate to the File menu and click "Project Structure"
(b) Click the "Libraries" tab
(c) Use the minus button (-) to remove any pre-existing library entries
(d) Click the "+" button, then click "Java" and direct the file browser to the "HAL-master/HAL/lib" folder. (e) Click apply or OK