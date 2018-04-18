# tdz

I started writing this command-line app that uses Google Calendar as a backend
to store TODO tasks that are scheduled, and helps you conveniently manage
rescheduling and following up on your tasks.

Luckily, I didn't get too far before I realized that [Taskwarrior][taskwarrior]
can already do what I need my app to do, minus the Google Calendar integration
which is really not important to me. (In fact, I took a [previous stab][ews] at
implementing my TODO manager app using a SQLite database, which was working out
OK, but it felt kind of clunky to have to write migrations and manage a
database, when it occurred to me that I could use an existing calendar service
as a backend. I also chose to try writing it in Rust, which slowed me down
because I'm not great at Rust!)

So, I've started using Taskwarrior instead, which means I can stop where I am on
this app. I'm pushing it to GitHub so that I have a backup of the code, in case
I ever need to leverage some of the work that I've done so far. Things I've done
so far that could be useful in the future:

* Kotlin command-line app boilerplate, including JCommander for arg parsing.

* Basic setup to be able to use the Google Calendar API on users' behalf, which
  is a bit complicated. (This includes the Setup section below.) I translated
  some clunky Java code into Kotlin (see GoogleCalendar.kt) and started building
  my own abstractions on top of it.

[taskwarrior]: https://taskwarrior.org
[ews]: https://github.com/daveyarwood/ews

## Setup

`tdz` requires some credentials authorizing it to use the Google Calendar API on
your behalf. To set this up, follow Step 1 of [this
guide](https://developers.google.com/calendar/quickstart/java) to create
credentials and download them as a JSON file.

Then, make a directory `~/.credentials/tdz-google-calendar` and place your
`client_secret.json` file there.

The first time you use `tdz`, you will be asked to authorize `tdz` to use the
Google Calendar API in your browser. After that, it will remember you so that
you don't have to re-authorize the application every time.

## License

Copyright © 2018 Dave Yarwood

Distributed under the Eclipse Public License version 2.0.
