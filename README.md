
# Clojure Queuing Example

A barebones Clojure app, which can easily be deployed to Heroku.  

This application support the [Getting Started with Clojure](https://devcenter.heroku.com/articles/getting-started-with-clojure) article - check it out.

## Running Locally

Make sure you have Clojure installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

Then [install RabbitMQ](http://www.rabbitmq.com/download.html) and make sure it's running.

Now download and start the app:

```sh
$ git clone https://github.com/heroku/clojure-queuing.git
$ cd clojure-queuing
$ lein repl
user=> (require 'clojure-queuing.web)
user=>(def server (clojure-queuing.web/-main))
```

Your app should now be running on [localhost:5000](http://localhost:5000/).

## Deploying to Heroku

```sh
$ heroku create
$ heroku addons:add cloudamqp
$ git push heroku master
$ heroku open
```

## Documentation

For more information about using Clojure on Heroku, see these Dev Center articles:

- [Clojure on Heroku](https://devcenter.heroku.com/categories/clojure)
