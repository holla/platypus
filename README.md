platypus
========
Believe in the platypus!

Change test

Our player doesn't play games with your heart.

![alt text](https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRp5phnGtV1Gky5aEBD5gDsHxMVALRIeEVVTBis7ufDkCW29hfY "I don't really!")

get started
===========

Pull the current code by 

`git clone https://github.com/holla/platypus.git`

philosophy of development
=========================

First of all, git is awesome and will save us headache, since we are a team of four, which is reasonably big.

####Learn how merge works with git
http://git-scm.com/book/en/Git-Branching-Basic-Branching-and-Merging

####Good branching practice
http://nvie.com/posts/a-successful-git-branching-model/

####what does it mean for us?
So, (1) if you are adding a small change, pushing to master is fine if you are certain that it will not break stuff, because master branch will always hold a working player. Players will be tagged by version numbers.

(2) if you are developing a big new feature, please create a new branch. Then we all get together and merge it back into master to make sure that nothing breaks.

(3) When we are making small incremental changes that will break stuff, we will create a "development" branch that will closely parallel that of master, and will routinely be merged with master to make sure that we do not have to merge the whole code base, which is quite painful.

(4) Line endings in windows and linux are different, so if you are committing from a windows machine, it will overwrite EVERYTHING. So it would be best if you used UNIX :) If not, then I can show you how to use UNIX line endings.

##How to merge
You are either developing on `master` or your own `branch` called `whatever`. If you reached a good point where everything works, then 
you should push to master. To do that

1. `checkout master`
1a. `git pull`
1b. If you have some conflicts, it will abort. More on this later... you have to resolve the merge or revert your changes.
2. `merge whatever`
3. `git push`
4.  yay!

To pull latest changes into your own branch,
1) `checkout master`
2) `git pull`
3) `git checkout whatever`
4) `git merge master`

Routinely do `git clean -fd` because git does not track directories -- just folders, so legacy directories will persist.

####updating wiki

Update this thing quite frequently if you are introducing philosophical changes to the player.

Run PlayerPanel, create a copy of your player and an opponent (e.g. Random) and then use the ServerPanel to test!

