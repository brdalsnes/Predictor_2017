# World predictor

The wisdom of the crowd is the idea that a large group of people can make better predictions than a single expert. Collecting a huge number of answers allows you to even out the inevitable noise and emotional investment that comes with a single answer.

This approach has proven to work in the past with [prediction markets](https://en.wikipedia.org/wiki/Prediction_market) like [Intrade](https://www.buzzfeed.com/andrewrice/the-fall-of-intrade-and-the-business-of-betting-on-real-life?utm_term=.wh4ojrV4m#.lkJzXJkgW). World Predictor is a simplified version of this: choosing one event equates “buying” one event and “selling” the other. The probability of an event is calculated using [Hanson’s logarithmic market scoring rule (LMSR)]({{ "/assets/mktscore.pdf" | absolute_url }}) which simulates the probability (or price) an event would have if it was traded on a market.

This project was developed as an Android app using Java. The app can be found [here](https://play.google.com/store/apps/details?id=com.brdalsnes.worldpredictor).
