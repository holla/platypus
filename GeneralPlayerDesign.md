General Player Design
=====================

Here's a couple things I was thinking about earlier, so I put them here:
*  In the class, we're going to end up writing several different styles of player and our general player will need to be able to switch between player types.  The easiest way to make this work would be to just have a different method for each variant of the player, but we could also create separate classes for each.

Timing
=======

To take care of timing issues, could we have the program select a move run in a new thread and then have the main thread pull the best move from some global variable when the time is up?  This would solve the issues mentioned in class of having to adapt the length of your loop, etc to ensure the player submits a move (the thread would of course be given timing info as well so it could make use of it, but I see no reason why it needs to mess with the exact timing of providing a move).
