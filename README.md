# transactions-authorizer

A application that authorizes a transaction for a account following a set of predefined rules. This exercise is part of Nubank's selection process.

This solution was written in Clojure and Leiningen as build and testing tool. I tried to use as few external libraries as possible to solve the problem. In this solution I sought to follow best development practices, such as TDD, and the most idiomatic way to program in Clojure. However, due to the lack of prior language experience, I was unable to apply the TDD process completely, also making sure that there are also more idiomatic and better ways to develop and test the same solution.

## The project layout

This project is arranged in 4 namespaces:

* **parser** - contains the input reader and parser of each operation;

* **authorizer** - handler of the business code, contains account creator, processor of each transaction and has checks for all business logic violations;

* **logger** - as its name says, it is responsible for showing in the terminal and writing to the output file each transaction processed.

* **violation** - is a entity to register the business logic violations in the account.

## The input file

The sample input file containing all operations to be performed is: `resources/operations`. A sample output file is in this directory too.

It contains an entry that tests all possible cases of the problem, but can be modified and have other transactions for your internal test cases. If needed, the sample output file can be removed to be generated a cleaner output file.

On each program execution a log is shown on terminal. Also, after the first run of the program an output file is created: `resources/operations-output`. It will also register all outputs of subsequent program executions.

## Build and running

There are 4 ways to build and run the program through Leiningen.

To build and run in Shell script and with Leiningen you have to make sure that Java and Leiningen is installed.

To Linux users, get lein shell script:

1. `$ cd /usr/local/bin`
2. `$ sudo wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein`
3. `$ sudo chmod +x /usr/local/bin/lein`

To Mac OS users, just use Homebrew:

1. `$ brew update && brew cask install java`
2. `$ brew install leiningen`

Note that the `$` symbol is only to represent that command is executed on a Linux/Mac OS terminal.

### Shell script

1. `$ chmod -x authorize.sh`
2. `$ ./authorize.sh`

### Leiningen

1. Run the command: `$ cat resources/operations | lein run`

### Docker

1. Build the Docker image: `$ docker build -t transactions-authorizer .`
2. Run the Docker image: `$ docker run -it --rm --name transactions-authorizer transactions-authorizer`

### Uberjar

1. Build the uberjar: `$ lein uberjar`
2. Add permissions (if necessary) to execute shell script: `$ chmod -x authorize-jar.sh`
3. Run shell script: `$ ./authorize-jar.sh`

## Testing

Tests have been created to ensure that the main features of this solution works, but I'm sure there are more cases and conditions to be tested and way more performatic.

To run the tests just run `lein test` and the output with the assertions will be shown in the terminal.