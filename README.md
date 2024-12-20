# CS-180-Team-Project

## Instructions
In order to compile the project, you need to type javac into the terminal followed by the java file name and .java. An example of this is javac TestFile.java. After you enter this line, the driver will be compiled and ready to run. Next, you must type java followed by the same file name to execute the program. An example of this is java TestFile. Following this, the program will run and you should see the input screen in the terminal below the line you just entered. Please follow the menus and input what you'd like to execute based on the program's offerings. If you want to see the database you must download mysql first, then in the terminal of your choosing type 

mysql-u accessuser -h localhost -p -> When instructed for a password typed 12345. You're in the database! Then type:
USE social_network -> social_network is the name of the database
SELECT * FROM <Name of the Table> -> There are 4 options: Users, Friends, Messages and Blocked. This command will showcase everything in that table. 

NOTE: Currently only Users have example data in them. We have tested and removed from the other 3 already. When testing there are two usernames that are currently applicable: Kaurman and JDerosa


## Contributions
Maninder - entire database.sql and the test cases for the sql file. I also worked on the implementation of the SQL in the java files. For phase 2, she made both server files, fixed some mistakes from phase 1.

Emily - created & coded User.java (signup, login, getters & setters). also edited a little bit of userprofileinterface to fit both userprofile.java & user.java. Wrote basics for directMessage.java. Also created junit test case to run with user.java (for signin & login methods). submitted everything on vocareum workspace for phase one.

Phase two: Edited send-messages & created delete-messages for direct-messages class. Coded UserClient.java

Justin - Created the interfaces for each social media class platform (user, friend, message) and the test cases for the interfaces. Additionally created the Friends class and the jUnit test case to run with it.
Phase 2 - Made sure that the methods in the other files existed in the User file to ensure that there were no missing methods. Coded test cases for the classes that did not have test cases(User.java and UserSever.java)

Adi - (Phase 1 - Phase2 )User Blocking: Added methods in User class to block and unblock users, preventing blocked users from sending messages. Message Restriction: Enabled users to restrict messages to friends only through setRestrictMessagesToFriendsOnly and isMessageRestrictionEnabled methods. Interface and Messaging Updates: Updated MessagingInterface to support message restrictions and modified directMessage to enforce both blocking and friend-only messaging rules.
Phase 2: I made the directMessage class thread-safe by synchronizing methods that access or modify the shared allMessages list. This ensures consistent and correct behavior when multiple threads interact with the messaging operations.

Compatibility Assurance: Ensured seamless integration across User, MessagingInterface, and directMessage classes.

## Classes
User- The user class holds all the contructers and methods related to the user. This includes, creating a user, logging in, viewing your own user. The User object will also have multiple attributes including:
- user_id: Unique identifier for each user.
- username: Unique username for the user.
- pass: Password for user authentication.
- email: Unique email address for the user.
- bio: A short string (up to 150 charecters) for user expression.
- created_at: Timestamp of when the user account was created.
(PART OF USER CLASS NOW)Friend- Uses a user object but is different from the User. The friend will hold all the methods that include all the adding friends, blocking friends, pending friend requests. The Friend object will also have the following attributes:
- user_id: ID of the user who initiated the friendship.
- friend_id: ID of the user being added as a friend.
- status: Status of the friendship (e.g., 'pending', 'accepted', 'blocked').
- created_at: Timestamp of when the friendship was created.
- Three main methods that will add, remove, and block other users on the platform
- Additional method isBlocked that will help check if a user is blocked
- Primarily made using 2D arrays storing values as the user and the users friends


Message - Holds all the messaging methods that allows communication between two users, or more (via groupchat). The Message will include the following attributes:
- message_id: Unique identifier for each message.
- sender_id: ID of the user sending the message.
- receiver_id: ID of the user receiving the message.
- content: The text content of the message.
- created_at: Timestamp of when the message was sent.
- is_read: Boolean indicating whether the message has been read.

## Class: User.java

### Methods (that have been implemented):
User() - Instantiates a user class
- username is set to ""
- password is set to ""

User(String username, String password, String email)
- username set to username
- password set to password
- email set to email
- user count increased by one

getPassword() - gets the password from the user

setPassword() - sets the password from the user

getUsername() - gets the user for the user

setUsername() - sets the user for the user

boolean login() - returns true if login works, false if login doesn't. allows user three attempts to login by matching whatever inputted username with the password. if either username doesn't exist or password doesn't match, system allows user to know.

void signUp() - allows a new user to sign up with specific username & password requirements. if successfuly signed up, user/password combo will be save into the 'accountInfo.txt' file.

boolean findUser() - a private method to find the user by going through the file accountInfo.txt using the scanner class.

addFriend(String user, String friendUser); - adds a friend based on the username inputted
** returns true if done, false if not

blockFriend(String user, String blockedUser) - blocks a friend based on the username inputted
** returns true if done, false if not

removeFriend(String user, String friendUser) - removes a friend based on the username inputted
** returns true if done, false if not

## Class: directMessage.java

sendMessage (User sender, User recipient, String message); - sends a message from sender to recipetent. returns true if successful, false if not
** NOT fully implemented

deleteMessage(String message, User username); - deletes specified message if found
** NOT FULLY IMPLEMENTED

blockUser(User sender, String username); - has username added to sender's blocked list
** returns true if done, false if not.

restricted(boolean restricted) - sets restricted to parameter.


## Database
We have created a database.sql which will only be implemnted once. This file will create tabls (another file has corresponding test cases). The sql table works like an excel spreadsheet. Each column is assigned a variable value (user_id, profile_pic, etc.), and each row corresponds with each of the user's created and currenly created within our social media platform. We have created example implmentations within the data files to showcase how varibles will be pushed. Later down the road, we will actually create this and get variables, set variables, etc. create.sql CREATES the database and database.sql actaully is the database in sql that was downloaded. It would be better shown as a .csv file later on. Creation of the database was made by mysql and the terminal, where the testcases were also ran on a backup database. To access the database, you must configure it. In each file there are instructions to do so. You can contact it from localhost through the username 'accessuser', port 3306, and the password 12345. 
