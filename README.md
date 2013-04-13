platypus
========

Our player doesn't play games with your heart.

get started
===========

Pull the current code by 
git clone https://github.com/holla/platypus.git

philosophy of development
=========================

First of all, git is awesome and will save us headache, since we are a team of four, which is reasonably big.

First, learn how merge works with git: http://git-scm.com/book/en/Git-Branching-Basic-Branching-and-Merging

It would be best if we could follow this policy loosely: http://nvie.com/posts/a-successful-git-branching-model/

So, (1) if you are adding a small change, pushing to master is fine if you are certain that it will not break stuff, because master branch will always hold a working player. Players will be tagged by version numbers.

(2) if you are developing a big new feature, please create a new branch. Then we all get together and merge it back into master to make sure that nothing breaks.

(3) When we are making small incremental changes that will break stuff, we will create a "development" branch that will closely parallel that of master, and will routinely be merged with master to make sure that we do not have to merge the whole code base, which is quite painful.

(4) Line endings in windows and linux are different, so if you are committing from a windows machine, it will overwrite EVERYTHING. So it would be best if you used UNIX :) If not, then I can show you how to use UNIX line endings.

updating wiki
=============

Update this thing quite frequently if you are introducing philosophical changes to the player.


