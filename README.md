"# Foodify" 

Prologue: I had 10 hours to build this at a hackathon and I am quite proud of what I accomplished

How to use: After getting your groceries, input a food item (currently only supports common fruits and vegetables). Input can be given in two ways:
1. Text input
2. Image recognition (Clarifai). Take a photo of your food and using the Food model from Clarifai it can accurately determine what food item it is.
After submitting the food item input, it will return a list of all types of that food (whether it is dried, cut up, fresh, etc). Upon selecting one of those options, it will redirect you to the next page that lists out all of your foods, how long they will last, and their expiry dates. Go back to the main menu to add more items!

Tools/Frameworks/Apis: This is an Android App so obviously it used the Android SDK. To use image recognition, I called the Clarifai API and used the food model. I also used jsoup for web scraping food expiration date data from google search results. 

TODO: I plan on continuing this project as I think it is a viable solution for many university students who forget about the expiration dates on their food; this is a common problem I see in my own house and in many others in my residence. 

I plan to improve this app to allow an additional feature for students to sell their almost expired food at a heavy discount to other fellow students. This will offer a cheaper alternative and there will be no food waste! I will do this by creating a website for users to sign up on and share their almost expired foods. I plan to work on this over Winter 2017-18. Check up back on here to see how it goes!

:)
