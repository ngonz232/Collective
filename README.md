Original App Design Project - README Template
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

* User can login
* User can create an account 
* User can select a listed event and register/unregister as a volunteer
* User can post a new volunteer event to the stream as well as remove it
* User can upload a profile picture using their phone's camera
* User can upload event pictures using their phones camera
* Each event has a "Date created" shown
* Ability to log out
* A profile page where the user can see their profile and events they registered for
* Google Maps API implemented to show where a particular event is located

**Optional Nice-to-have Stories**

* User can see a list of local volunteers in card view
* The volunteers only appear in the list if they mark themselves as public (privacy settings)
* User can use a search bar to search volunteers and events
* Each event has a detailed view
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
   * Google Maps API implemented to show where a particular event is located

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
   * Detailed view (if implemented)
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
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
