# Test projects for wes JWA1

This repository consists of project that tests whether a given Java WebSocket API implementation is able to work with wes Java WebSocket API. It's true that any implementation properly implementing JWA 1 works with wes JWA and that's why there is no test per server in wes repository. But unfortunately some implementations are not. By running each test, you can see which implementation does pass the test and make clear your decision.

---

These test projects are private so you have to clone the repository or download it. 
```
git clone https://github.com/flowersinthesand/wes-jwa1-test.git
cd wes-jwa1-test
```

Unless otherwise stated, the server will be managed as dependency and executed in embedded mode.

```
cd jetty9
mvn test
```
