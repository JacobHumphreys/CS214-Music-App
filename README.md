[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=19459464)
# CS-214 Music App
# Motivation
For the final iteration of the CS-214 song application project, I wanted to focus on improving the
experience of using the application, rather than focusing on adding wild new functionality. Thus, 
when I contemplated how I could go about doing so, I decided that it would be sufficently useful to
graphicaly integrate the process of creating and editing the csv files that the application imports.  

This system will be built upon the already existing interactive cli and will be designed to mimic
the style and behavior already present in the application in order to unify the user experience.  

The use cases I will target with this are the following:  
1. Creating new csv files and populating the new file with user song data
2. Adding/Editing data in existing imported csv files.
3. Editing data contained in existing imported csv files in bulk.

# Tasks
- Refactor UI internal design.
- Implement menus for csv editing. This should include a user select, song select, and rating prompt.  
**NOTE**: All menus should allow for new data to be entered as well, so there should also be user and song creation options and associated prompts.
- Implement internal process of modifying database information.  
**NOTE**: User modificaitons should be applied to the runtime version of the application.
- Implement file creation and a way of exporting database information in the format of the input CSVs.
- Implement bulk editing of database information.
- Ensure all tests and PDM checks are passing.

# How to Compile and Run
The changes will be implemented in the interactive mode of the program. Additional menu prompts will be made avaliable on the file options menu.  
Just as in pa6, you can enter interactive mode with  
```gradle run -q --console=plain --args="-i"```

# Retrospective

### What went well?
I found the process of implementing the new features uniquely challenging, not because the features
themselves were complex but rather because the addition of the new menus lent towards the creation
of abstractions that did not make sense in the earlier PA. I spent a very solid amount of time
refactoring the code that is used to design the interacitve application and I feel the changes
simplified the control flow significantly.

Unfortunately these changes temporarily broke my entire testing suite, but I was able to fix the issues rather quickly.

### What could be improved?
I think more features for the csv editor would have been nice to implement such as user/song 
deletion from the dataset. Unfortunately the scope of the features already present resulted in a 
lack of time for some of my stretch goals. Additionally, I think if I were to add more to the project I would 
attempt to break up the menus package into further subpackages because it is currently very bloated with files for each menu.
