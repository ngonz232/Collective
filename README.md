Original App Design Project - Collective
===

# Collective

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
The Collective app aims to harness the power of social media and use it to build social value and commmunity by having a platform where members of a community can come together to volunteer for local needs/events in their community. 

### App Evaluation
- **Category:** Social Networking 
- **Mobile:** Exclusively a mobile experience at initial launch. It would be using the camera to upload pictures of the events and users, and it would use location as well to see nearby needs for volunteers.
- **Story:** Allows users to post local needs for volunteer services in their community as well as to sign up for posted volunteer events. It aims to harness collective power through social networking and make users come together towards one goal. 
- **Market:** The app encourages everyone to participate, and there is no niche market as we aim to bring everyone together towards a common goal. The potential scope of the user base would be worldwide since every community could participate. 
- **Habit:** This app would become a part of everyone's volunteering habits and users would check daily for any new opportunities. Many people have volunteering and philantrophy as a part of their lives' schedule. They would use this to plan it out.
- **Scope:** It could start out as a simple posting service where users could post and sign up for volunteer needs in their community, but it can eventually expand to be included as a feature on social media platforms to partner with local companies and governments to use as a way to manage volunteer hours. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can login (Week One)
* User can create an account (Week One)
* Ability to log out (Week One)
* User can post a new volunteer event to the stream as well as remove it (Week Two)
* User can select a listed event and register/unregister as a volunteer (Week Two)
* User can upload event pictures using their phones camera (Week Two)
* Event's description is limited to a certain number of characters in the stream view. Detailed view shows full description.
* User can upload a profile picture using their phone's camera (Week Three)
* A profile page where the user can see their profile and events they registered for (Week Three)
* Google Maps API implemented to show where a particular event is located (Week Three)
* Each event has a "Date created" shown (Week Three)
* User can filter events by either most recent date or nearest first (Week Three)
* User can press on an event to be taken to a detailed view showing the Google Maps (Week Three)
* User can swipe left to delete an event from the view (Week Three)
* Zoom in animation when user transitions to detailed view (Week Three)

**Optional Nice-to-have Stories**

* User can see a list of local volunteers in card view
* The volunteers only appear in the list if they mark themselves as public (privacy settings)
* User can use a search bar to search volunteers and events
* Each user profile card can be expanded into a detailed view
* User can only remove their own posted events
* User can only unregister themselves as a volunteer not others
* User can only update their own pictures on events and profiles
* User can add skills to their own profile
* User can specify skills needed when creating an event
* A map page using the Google Maps API that shows ALL events near you
* Animations between pages and a ripple effect on volunteer profile cards
* A nice UI with gradient logos and backgrounds

### 2. Screen Archetypes

* Login
   * User can login
* Create an account
   * User can create an account

* Event stream view
   * User can select a listed event and register/unregister as a volunteer
   * Each event has a "Date created" shown
   
* Event detailed view
  * Google Maps API implemented to show where a particular event is located
  * Extended description is shown   

* Add or edit an event
  * User can post a new volunteer event to the stream as well as remove it
  * User can upload event pictures using their phones camera

* User profile view
  * User can upload a profile picture using their phone's camera
  * Ability to log out
  * A profile page where the user can see their profile and events they registered for
   

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream
* Add an event
* Profile

**Flow Navigation** (Screen to Screen)

* Login
   * Event Stream
* Create an account
   * Event Stream
* Event Stream View
   * Detailed view 
* Add an event
   * Event Stream View
* Profile
   * Edit profile
   * Login (if user logs out)
  

## Wireframes
<img src="/Collective-1.png" width = "100" height = "200"> &nbsp; <img src="/Collective-2.png" width = "100" height = "200"> &nbsp; <img src="/Collective-3.png" width = "100" height = "200"> 
## Create an account! / Log in screen / Event Stream
<img src="/Collective-4n.png" width = "100" height = "200"> <img src="/Collective-5.png" width = "100" height = "200"> <img src="/Collective-6.png" width = "100" height = "200">
## Event Detailed View / Post an Event / User Profile
 
## Schema 

### Models

* User

| Property    | Type   | Description                    |
| ----------- | ------ | ------------------------------ |
| Username    | String | User's registered username     |
| Password    | String | User's selected password       |
| fullName    | String | User's registered name         |
| Gender       | String | User's selected gender       |
| Email       | String | User's registered email        |
| phoneNumber | String | User's registered phone number |
| dateOfBirth            | String       | User's registered date of birth                               |
| profilePicture            | File       |User's provided profile image                                |



* Post

| Property      | Type            | Description                                 |
| ------------- | --------------- | ------------------------------------------- |
| authorId      | Pointer to user | Pointer to event's author                   |
| eventId       | String          | Unique id for the user post (default field) |
| Image         | File            | Event's picture uploaded by author          |
| eventName     | String          | Author-provided name                        |
| numVolunteers | Number          | Number of volunteers needed                 |
| Date          | String          | Date of event provided by author            |
| Description   | String          | Description of event from author            |
| Organizer              |    String             |   Name of event organizer/organization                                          |
| Location      | String          | Event Address Provided by author            |

### Networking
#### List of network requests by screentab
   * Login Screen
     - (POST) User's entered login information to DB
     - (Read/GET) Query the information in the DB. If it matches a user, authorize access. If not, revoke access.
     
   * Create an Account Screen
     - (POST) User's entered information to create a new user object.  
     
   * Home Feed Screen
     - (Read/GET) Query all posted events
     - (Create/POST) Register for an event
     - (Delete) Delete existing event
     - (POST) Event address to Google Maps API/Make event address clickable directing to a map
     - (Read/GET) Get user's location from DB
     - (Sort/Filter) User can sort by recent date or nearest event to their location
     
     ##### Example Code Syntax Snippet for our actions.
     let query = PFQuery(className:"Post")
     query.whereKey("author", equalTo: currentUser)
     query.order(byDescending: "createdAt")
     query.findObjectsInBackground { (posts: [PFObject]?, error: Error?)      in
      if let error = error { 
      print(error.localizedDescription)
      } else if let posts = posts {
      print("Successfully retrieved \(posts.count) posts.")
      // TODO: Do something with posts...
      }
      }
      
* Event Detailed View
     - (Read/GET) query selected event object
     - (POST) Event location to Google Maps API
     - (GET) Event geolocation marker on Google Maps API

* Post Event Tab
     -  (Create/POST) Create a new event object
     -  (PUT) Uploading event image
* Profile Screen
     - (Read/GET) Query logged in user object
     - (Read/GET) Query all posts where user is registered/author
     - (Update/PUT) Update user profile image
     - (Clear) Destroy current session/User can log out

#### Google Maps SDK/API


| HTTP Verb | End Point     | Description                                                                                            |
| --------- | ------------- | ------------------------------------------------------------------------------------------------------ |
| GET          |  /getLatitude             | Converts an address to lat,long to pass to our map to display an event on it with a geoLocation marker                                                                                                       |
| GET       | /getLongitude | Converts an address to lat,long to pass to our map to display an event on it with a geoLocation marker |
## Gestures and Animations
- Swipe left to delete posted event from list view (Delete button will show upon swiping left)
- Zoom in animation to transition to the "Event Detailed View" upon clicking
## Complex Features
* Required stories
   - (Sort/Filter) We will query all posted events in the event stream and then the user can do a sort and filter call to the DB to display them by nearest location or most recently posted. This counts as a complex feature because we are using multiple calls and steps
* Optional Stories
   - (Sort/Filter) User can use a search bar to search volunteers and events
   - (Sort/Filter) The volunteers only appear in the list if they mark themselves as public (privacy settings)
