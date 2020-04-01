# Workspace

## A consolidated launchpad and organizer for your projects

Workspace is intended to improve focus and productivity. Work is organized into "spaces", where each space consists of 
one category or block of work. It has wide applicability because one space could be a University class, work project,
personal hobby, etc. Workspace lets you compile all relevant files, sites, to-dos, and more into one space. When you
begin working, you enter a space and set a timer for how long you intend to work. Workspace eliminates distractions and
provides you with network relevant to your space, helping you focus. 

The program is especially useful for students, office workers, or self-employed people, as it helps promote focused and 
efficient work. However, its functions can also be used for personal projects and hobbies, as the user can easily set up 
everything they need on their computer for one hobby with a few mouse clicks.

This project is of interest to me mainly because it would be very useful for me. I always have a browser 
window open for each course with all relevant sites open, but this is messy and also disappears if I restart my
computer. I believe that being able to link everything relevant to one course and give myself a set time to work on it
is very useful. It is also of interest to me because I will get to learn how to have my application interact with
other applications, such as by opening links and app shortcuts.

### Functionality

**Base features that will be included in each space:**
- Pin relevant website links, with button to quickly open all in new browser window
- Pin relevant directories, with buttons to open pinned files or visit directories in file explorer
- Pin shortcuts to relevant applications
- To-do list
- Start timer when you begin working, which plays a sound once finished
  - Once time is up, user can choose to add time, stop working, or time a short break

**Additional features that could be added as time allows:**
- To-do list can have due-dates and priority
- Account to back up data online
- Collaborative group spaces
  - Spaces are accessible by invited group members
  - Group chat within space
  - File sharing
  - Collaborative to-do list
  - Members can see when other members are working in this space, or busy working in another space
- Option to minimize or close all non-relevant apps and sites
- A notification will appear if an unpinned application is opened or if blacklisted sites are visited in order to deter
distraction

### User Stories

- As a user, I want to be able to create a new space and add it to a list of spaces
- As a user, I want to be able to see a list of all spaces
- As a user, I want to be able to add a website link, file path, or application to a list of resources in a space 
(for an application, this means adding the path to an executable file)
- As a user, I want to be able to open my resources (links, files, apps) in an external program
- As a user, I want to be able to start a timer for time to work in a space
- As a user, I want to be able to add a task to a to-do list within a space
- As a user, I want to be able to mark a task as complete or delete it from a space
- As a user, I want to be able to save all of the information in my spaces when I quit the application
- As a user, I want my previously saved space data to be automatically loaded when I open the application
- As a user, I want to be able to save my space data without quitting the application
- As a user, I want to be able to backup my data to an online database
- As a user, I want to be able to restore data that is backed up online

## Phase 3 Instructions for Grader
- Load the app by running main. It will preload some sample save data.
- You can generate the first required event by clicking the plus button in the bottom right corner to add a new space
- You can generate the second required event by clicking one of the listed spaces to display its contents 
- You can trigger my audio component by clicking set time under the timer and setting the time to 0:00 (just so you 
don't have to wait for the timer to tick down) and pressing start, the time will immediately be up and a chime will play.
- You can save the state of my application by pressing the exit button to return to the main menu, then selecting 
Save > Save locally in the top menu bar (or backup online).
- You can reload the state of my application by selecting Load > Load local save in the top menu bar (or restore online backup).

## Phase 4
# Task 2
Type hierarchy: WebsiteLink and FilePath implement Resource (and AppShortcut extends FilePath)
An example of an overridden method implemented differently is setPath(); in FilePath it checks that the path
leads to a valid file, while in WebsiteLink it checks that the path is a valid URL.