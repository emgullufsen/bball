# bball

[![bball-CI](https://github.com/emgullufsen/bball/actions/workflows/main.yml/badge.svg)](https://github.com/emgullufsen/bball/actions/workflows/main.yml)

A simple NBA Live Scoreboard webapp. It uses an initial AJAX request to data.nba.net, then opens a websocket connection to the clojure backend, which feeds
score updates to the client every five seconds.

[Live App](https://nba-scores.rickysquid.org)

[NBA Data](http://data.nba.net/10s/prod/v1/today.json)

## License

GNU GPLv3 - See COPYING file

Copyright © 2021 Eric Gullufsen
