FROM clojure
RUN mkdir -p /usr/src/transactions-authorizer
WORKDIR /usr/src/transactions-authorizer
COPY project.clj /usr/src/transactions-authorizer/
RUN lein deps
COPY . /usr/src/transactions-authorizer
RUN chmod +x authorize-jar.sh
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" transactions-authorizer-standalone.jar
CMD ["./authorize-jar.sh"]
