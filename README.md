# Minesweeper API

### Reference Documentation

This API allows users to play the beloved retro minesweeper game. The features of this API are:

* Rol up new users. By creating your user, you will be able to play as much games as you want.
* Create custom boards. You will be able to create boards of different sizes (max 10*10) with max 100 bombs.
* Flag cells. Your can mark/unmark those cells you suspect contains bombs.
* Resume previous games. You can stop playing and resume the game whenever you desired.
* Review the statistics of your games. You can see the characteristics of any board created and played, seeing time invested and if the games was won or not.

They are two roles available in this API:
1. **USER:** default type when users are created and allow creating and play games
2. **ADMIN:** Users rights plus querying the games from all Users.
### API endpoints

The following list depict the available endpoints of the API:

1. Authentication

   * [User Sign Up (POST)](http://localhost:8082/api/v1/auth/users) : *Allow to create a new user in the system.*
                   
     Request:

       ```
        curl --location --request POST 'http://localhost:8082/api/v1/auth/users' \
             --header 'Content-Type: application/json' \
             --header 'Accept: application/json' \
             --header 'If-None-Match: 098e97de3b61db55286f5f2812785116f' \
             --data-raw '{
                            "username": "scott2",
                            "password": "tiger",
                            "firstName": "Bruce",
                            "lastName": "Scott2",
                            "email": "bruce2@scott.db",
                            "role": "ROLE_ADMIN"
                          }'
       ```
     Response:

       ```
         {
             "links": [
                 {
                    "rel": "user-signin",
                    "href": "http://localhost:8082/api/v1/auth/token"
                 }
             ],
             "id": "443fc613-430e-42eb-8ba8-6709a803cb45",
             "username": "scott2",
             "firstName": "Bruce",
             "lastName": "Scott2",
             "email": "bruce2@scott.db",
             "password": "Ciphered...",
             "role": "ROLE_ADMIN"
         }
       ```
   * [User Sign In (POST)](http://localhost:8082/api/v1/auth/token): *Allow user Authentication.*

     Request:

          curl --location --request POST 'http://localhost:8082/api/v1/auth/token' \
                --header 'Content-Type: application/json' \
                --header 'Accept: application/json' \
                --header 'If-None-Match: "0a54ae579d2f1e6a7050d6775e602a9ab' \
                --data-raw '{
                               "username": "scott2",
                               "password": "tiger"
                            }'

     Response:

           {
             "links": [
                        {
                           "rel": "user-team",
                           "href": "http://localhost:8082/api/v1/team/443fc613-430e-42eb-8ba8-6709a803cb45"
                        }
                      ],
             "refreshToken": "m92k6sl4cdm3lv08adu9suce1lhp47l73cue518e6a3m0pb12ool6gp7dei0bpepdr4bncpsbtcoftduldjd7fdnq1brg1t5a7dqqatvo24tratmn9favv09ibur226d",
             "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODM4NywiaWF0IjoxNjM5Nzc3NDg3fQ.G-bpC59K4nPvZ7xYxTJ67JCUm41IZUE70AviRtk2RUBb2E4X5y0qjr08LPTJpkl-5c7CqkBC7XkyEtLPITYfFd-vs0AIB1JkfuLIykvte1QZ6jTjfdgteqX7QXOJfd0n5xjoXcWT0Z_oR84t9OSSLpneNpROKrOvROyxf6mrNK7M8FnNnzTDqk-fDHBbNrCZKG1GX2PawGocnOUw_qPd2Y08MVoaYzHj61MAKsqTn_9rEViY0T9mRLCgTaekHewN9dAdIGsg7UR0s78nY7Hnd8d3bcPeLLeR1o3tZwyXCqBML0hPQ9NyvmuPmepKbgtEmJOMNhLKT4Fk9yaU5IXnlb7WWkmboFsIm-gIOWfYSgW2Qv1RjVwx-aCZ5sxFACcIrqIBVgLiYMrulzKzVxZFyMSLDW3SrJKuT8DYABqiY6kuoGopx_vjL_3J-bEdhoZrTEJ6JweEIt1LQiYD0deQpL61EsolkYriJgKpxn5VNhFexn6IacHfLZP11jc0e95Wu-Rlc1cbt4tCUMepY0SibHHkHL6B-5AzCeQrWZkIxh1HIhO23bZ72_lZDHzeteHotXFqCaXUGqo5wS4M7uBNpUgp1JL8IW68SmbTXlnhWeyQI3Ybdh_f3QDqZGyizMm3AIy42tPRgBHsA3Swpcrlb50DTg8QEajlW18MbAUFs28",
             "username": "scott2",
             "userId": "443fc613-430e-42eb-8ba8-6709a803cb45"
        }       

   * [User Refresh Tokens (POST)](http://localhost:8082/api/v1/auth/token/refresh): *Allow refresh the JWT access token.*

     Request:
   
           curl --location --request POST 'http://localhost:8082/api/v1/auth/token/refresh' \
                --header 'Content-Type: application/json' \
                --header 'Accept: application/json' \
                --header 'If-None-Match: "0a54ae579d2f1e6a7050d6775e602a9ab' \
                --data-raw '{
                               "refreshToken": "m92k6sl4cdm3lv08adu9suce1lhp47l73cue518e6a3m0pb12ool6gp7dei0bpepdr4bncpsbtcoftduldjd7fdnq1brg1t5a7dqqatvo24tratmn9favv09ibur226d"
                           }'
   
     Response:
   
           {
             "links": [],
             "refreshToken": "m92k6sl4cdm3lv08adu9suce1lhp47l73cue518e6a3m0pb12ool6gp7dei0bpepdr4bncpsbtcoftduldjd7fdnq1brg1t5a7dqqatvo24tratmn9favv09ibur226d",
             "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODY4NywiaWF0IjoxNjM5Nzc3Nzg3fQ.Jud34UpYN1d1L_NSVGvgmLraXmiGf5uTjQI5tIUp9PjeP9UEc70SQcy-WlDX1-hO0P8bxyfuOy48eH0D169CIIEGs4I87qgR8HugQFvzuw4vaZiiDH4JX-By-BuSdm6-NDg5XjS3EHBtGnE3IID7qgAA0DHaPp-cZZ82bsyCGbDm8OhwA6uMPwZSG-oariWULtBGds44iTpXivKC8dutvwVvoKcfVP1FTdB9YHwfULabqZ-PmYkoI8cI_Jd6p97VdzBfin2bg-56s0UxbPeGs5LcYnFgNp-gWOYlNqaeF9QMlsKinnm0wbhWRWgu6EAW-UdwcKeLGN4r9skZ01BtneQaeDNWOGak9119gPU5xHlx8Vu1Cz1VCGzyMdvmcU4SKAq7JYCthUBkTeXSGPO3IzRdgGb_86o3XptObZvDssbeSV-cAhjjz9UiuIVbrkeEZD0frd6GfyRig3NMUdlTQOu9Uj0Pyh85a1cqX_nh-dKV0EFOyXrhIlAr9AHDwAyx6lqfD3tl8bjzgID5bSUsA0ZTmZxWgpiz4BnLF3otBGkHe62hCHZltD8_3G8GxRkC88mwYgPGehlcZH91LZs6lfwTLqn0gejiaAdcJTde5FFVXvJkMd6H07ELwW01w27XyRGRB4wUTdu_2uCzC27rUltu0PYYk3MJs-J_eDAEjpY",
             "username": "scott2",
             "userId": "443fc613-430e-42eb-8ba8-6709a803cb45"
           }
   
   * [User Sign Out (DELETE)](http://localhost:8082/api/v1/auth/token): *Allow ending the user session.*

     Request:

           curl --location --request DELETE 'http://localhost:8082/api/v1/auth/token' \
                --header 'Content-Type: application/json' \
                --header 'Accept: application/json' \
                --header 'If-None-Match: "0a54ae579d2f1e6a7050d6775e602a9ab' \
                --data-raw '{
                                "refreshToken": "m92k6sl4cdm3lv08adu9suce1lhp47l73cue518e6a3m0pb12ool6gp7dei0bpepdr4bncpsbtcoftduldjd7fdnq1brg1t5a7dqqatvo24tratmn9favv09ibur226d"
                           }'
       
     Response:
       ```
          No content
       ```
2. Game
   * [Create New Game (POST)](http://localhost:8082/api/v1/game/): *Create new custom board game.*

     Request:

       ```
        curl --location --request POST 'http://localhost:8082/api/v1/game/' \
             --header 'Content-Type: application/json' \
             --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODY4NywiaWF0IjoxNjM5Nzc3Nzg3fQ.Jud34UpYN1d1L_NSVGvgmLraXmiGf5uTjQI5tIUp9PjeP9UEc70SQcy-WlDX1-hO0P8bxyfuOy48eH0D169CIIEGs4I87qgR8HugQFvzuw4vaZiiDH4JX-By-BuSdm6-NDg5XjS3EHBtGnE3IID7qgAA0DHaPp-cZZ82bsyCGbDm8OhwA6uMPwZSG-oariWULtBGds44iTpXivKC8dutvwVvoKcfVP1FTdB9YHwfULabqZ-PmYkoI8cI_Jd6p97VdzBfin2bg-56s0UxbPeGs5LcYnFgNp-gWOYlNqaeF9QMlsKinnm0wbhWRWgu6EAW-UdwcKeLGN4r9skZ01BtneQaeDNWOGak9119gPU5xHlx8Vu1Cz1VCGzyMdvmcU4SKAq7JYCthUBkTeXSGPO3IzRdgGb_86o3XptObZvDssbeSV-cAhjjz9UiuIVbrkeEZD0frd6GfyRig3NMUdlTQOu9Uj0Pyh85a1cqX_nh-dKV0EFOyXrhIlAr9AHDwAyx6lqfD3tl8bjzgID5bSUsA0ZTmZxWgpiz4BnLF3otBGkHe62hCHZltD8_3G8GxRkC88mwYgPGehlcZH91LZs6lfwTLqn0gejiaAdcJTde5FFVXvJkMd6H07ELwW01w27XyRGRB4wUTdu_2uCzC27rUltu0PYYk3MJs-J_eDAEjpY' \
             --data-raw '{
                           "userId": "4bcad658-444f-45f1-8fef-613a86d794b0",
                           "numRows": 2,
                           "numColumns": 4,
                           "numBombs": 1
                         }'
       ```
     Response:

       ```
         {
           "id": "84777f85-92d1-406f-a5a1-c90ed108fe7e",
           "user": {
                     "id": "4bcad658-444f-45f1-8fef-613a86d794b0",
                     "username": "scott2",
                     "firstName": "Bruce",
                     "lastName": "Scott2",
                     "email": "bruce2@scott.db",
                     "password": "Ciphered...",
                     "role": "ROLE_ADMIN",
                     "links": [
                               {
                                 "rel": "user-signin",
                                 "href": "http://localhost:8082/api/v1/auth/token"
                               }
                     ]
                    },
           "rows": 2,
           "columns": 4,
           "numBombs": 1,
           "numCellsOpened": 0,
           "startTime": "2022-01-05T06:15:03.547647300Z",
           "endTime": null,
           "durationInSegs": 0,
           "gameOver": false,
           "won": false,
           "links": [
                     {
                      "rel": "get-gameForUser",
                      "href": "http://localhost:8082/api/v1/game/4bcad658-444f-45f1-8fef-613a86d794b0/84777f85-92d1-406f-a5a1-c90ed108fe7e"
                     }
                    ]
        }
       ```
   * [Get Game Of User (GET)](http://localhost:8082/api/v1/game/{{userId}}/{{gameId}}): *Retrieve specific game's board of the login user.*

     Request:

       ```
        curl --location --request GET 'http://localhost:8082/api/v1/game/4bcad658-444f-45f1-8fef-613a86d794b0/84777f85-92d1-406f-a5a1-c90ed108fe7e' \
             --header 'Content-Type: application/json' \
             --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODY4NywiaWF0IjoxNjM5Nzc3Nzg3fQ.Jud34UpYN1d1L_NSVGvgmLraXmiGf5uTjQI5tIUp9PjeP9UEc70SQcy-WlDX1-hO0P8bxyfuOy48eH0D169CIIEGs4I87qgR8HugQFvzuw4vaZiiDH4JX-By-BuSdm6-NDg5XjS3EHBtGnE3IID7qgAA0DHaPp-cZZ82bsyCGbDm8OhwA6uMPwZSG-oariWULtBGds44iTpXivKC8dutvwVvoKcfVP1FTdB9YHwfULabqZ-PmYkoI8cI_Jd6p97VdzBfin2bg-56s0UxbPeGs5LcYnFgNp-gWOYlNqaeF9QMlsKinnm0wbhWRWgu6EAW-UdwcKeLGN4r9skZ01BtneQaeDNWOGak9119gPU5xHlx8Vu1Cz1VCGzyMdvmcU4SKAq7JYCthUBkTeXSGPO3IzRdgGb_86o3XptObZvDssbeSV-cAhjjz9UiuIVbrkeEZD0frd6GfyRig3NMUdlTQOu9Uj0Pyh85a1cqX_nh-dKV0EFOyXrhIlAr9AHDwAyx6lqfD3tl8bjzgID5bSUsA0ZTmZxWgpiz4BnLF3otBGkHe62hCHZltD8_3G8GxRkC88mwYgPGehlcZH91LZs6lfwTLqn0gejiaAdcJTde5FFVXvJkMd6H07ELwW01w27XyRGRB4wUTdu_2uCzC27rUltu0PYYk3MJs-J_eDAEjpY'
       ```
     Response:

       ```
         {
           "details": {
                        "id": "635a15d6-528a-4fa1-90fa-5a113a375fad",
                        "user": {
                                  "id": "cab0de87-aef1-427b-96c9-1b83f30ead74",
                                  "username": "scott2",
                                  "firstName": "Bruce",
                                  "lastName": "Scott2",
                                  "email": "bruce2@scott.db",
                                  "password": "Ciphered...",
                                  "role": "ROLE_ADMIN",
                                  "links": [
                                            {
                                              "rel": "user-signin",
                                              "href": "http://localhost:8082/api/v1/auth/token"
                                            }
                                           ]
                               },
                        "rows": 2,
                        "columns": 2,
                        "numBombs": 1,
                        "numCellsOpened": 0,
                        "startTime": "2022-01-04T23:25:12.352235Z",
                        "endTime": null,
                        "durationInSegs": 0,
                        "gameOver": false,
                        "won": false,
                        "links": [
                                  {
                                    "rel": "get-gameForUser",
                                    "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74/635a15d6-528a-4fa1-90fa-5a113a375fad"
                                  }
                                 ]
                     },
           "board": [
                     [
                      {
                        "id": "285cfed0-49d2-401f-8d1e-d396081e6437",
                        "row": 0,
                        "column": 0,
                        "value": 1,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      },
                      {
                        "id": "be07123c-a384-4673-b698-ce523a0603ee",
                        "row": 0,
                        "column": 1,
                        "value": 1,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      },
                      {
                        "id": "b2d4dd78-2783-420d-86d6-5c0e576c1e44",
                        "row": 0,
                        "column": 2,
                        "value": 1,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      },
                      {
                        "id": "e645868c-a435-4738-9e16-64d02404662b",
                        "row": 0,
                        "column": 4,
                        "value": 0,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      }
                     ],
                     [
                      {
                        "id": "48bafbd1-6915-4ed7-b122-f316a156ca3a",
                        "row": 1,
                        "column": 0,
                        "value": 1                     ,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      },
                      {
                        "id": "ee8a99c4-b174-4cb0-b19e-78313adce1b7",
                        "row": 1,
                        "column": 1,
                        "value": -1,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      },
                      {
                        "id": "0ab70e06-908c-4af3-b6ae-4a9cd71e5053",
                        "row": 1,
                        "column": 2,
                        "value": 1,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      },
                      {
                        "id": "0ab1d111-fa3c-4bbf-829d-c09eb2df0aeb",
                        "row": 1,
                        "column": 3,
                        "value": 0,
                        "flagged": false,
                        "opened": false,
                        "detonated": false
                      }
                     ]
                    ],
           "links": [
                     {
                        "rel": "self",
                        "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74/635a15d6-528a-4fa1-90fa-5a113a375fad"
                     }
                    ]
         }
       ```
   * [Get All User's Games (GET)](http://localhost:8082/api/v1/game/{{userId}}?page=1&size=1): *Retrieve all the games created by the user.*

     Request:

       ```
        curl --location --request GET 'http://localhost:8082/api/v1/game/4bcad658-444f-45f1-8fef-613a86d794b0?page=1&size=1' \
             --header 'Content-Type: application/json' \
             --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODY4NywiaWF0IjoxNjM5Nzc3Nzg3fQ.Jud34UpYN1d1L_NSVGvgmLraXmiGf5uTjQI5tIUp9PjeP9UEc70SQcy-WlDX1-hO0P8bxyfuOy48eH0D169CIIEGs4I87qgR8HugQFvzuw4vaZiiDH4JX-By-BuSdm6-NDg5XjS3EHBtGnE3IID7qgAA0DHaPp-cZZ82bsyCGbDm8OhwA6uMPwZSG-oariWULtBGds44iTpXivKC8dutvwVvoKcfVP1FTdB9YHwfULabqZ-PmYkoI8cI_Jd6p97VdzBfin2bg-56s0UxbPeGs5LcYnFgNp-gWOYlNqaeF9QMlsKinnm0wbhWRWgu6EAW-UdwcKeLGN4r9skZ01BtneQaeDNWOGak9119gPU5xHlx8Vu1Cz1VCGzyMdvmcU4SKAq7JYCthUBkTeXSGPO3IzRdgGb_86o3XptObZvDssbeSV-cAhjjz9UiuIVbrkeEZD0frd6GfyRig3NMUdlTQOu9Uj0Pyh85a1cqX_nh-dKV0EFOyXrhIlAr9AHDwAyx6lqfD3tl8bjzgID5bSUsA0ZTmZxWgpiz4BnLF3otBGkHe62hCHZltD8_3G8GxRkC88mwYgPGehlcZH91LZs6lfwTLqn0gejiaAdcJTde5FFVXvJkMd6H07ELwW01w27XyRGRB4wUTdu_2uCzC27rUltu0PYYk3MJs-J_eDAEjpY'
       ```
     Response:

       ```
         {
           "links": [
                     {
                       "rel": "first",
                       "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74?page=0&size=1"
                     },
                     {
                       "rel": "prev",
                       "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74?page=0&size=1"
                     },
                     {
                       "rel": "self",
                       "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74?page=1&size=1"
                     },
                     {
                       "rel": "last",
                       "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74?page=1&size=1"
                     }
                    ],
           "content": [
                       {
                         "details": {
                                      "id": "635a15d6-528a-4fa1-90fa-5a113a375fad",
                                      "user": {
                                                "id": "cab0de87-aef1-427b-96c9-1b83f30ead74",
                                                "username": "scott2",
                                                "firstName": "Bruce",
                                                "lastName": "Scott2",
                                                "email": "bruce2@scott.db",
                                                "password": "Ciphered...",
                                                "role": "ROLE_ADMIN",
                                                "links": [
                                                          {
                                                            "rel": "user-signin",
                                                            "href": "http://localhost:8082/api/v1/auth/token"
                                                          }
                                                         ]
                                              },
                                      "rows": 2,
                                      "columns": 4,
                                      "numBombs": 1,
                                      "numCellsOpened": 0,
                                      "startTime": "2022-01-04T23:25:12.352235Z",
                                      "endTime": null,
                                      "durationInSegs": 0,
                                      "gameOver": false,
                                      "won": false,
                                      "links": [
                                                {
                                                  "rel": "get-gameForUser",
                                                  "href": "http://localhost:8082/api/v1/game/cab0de87-aef1-427b-96c9-1b83f30ead74/635a15d6-528a-4fa1-90fa-5a113a375fad"
                                                }
                                               ]
                                    },
                       }  
                      ],
           "page": {
                     "size": 1,
                     "totalElements": 2,
                     "totalPages": 2,
                     "number": 1
                   }
         }
       ```
   * [Open Cell (PATCH)](http://localhost:8082/api/v1/game/cell): *Reveals the content of a cell in the playing board.*

     Request:

       ```
        curl --location --request PATCH 'http://localhost:8082/api/v1/game/cell' \
             --header 'Content-Type: application/json' \
             --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODY4NywiaWF0IjoxNjM5Nzc3Nzg3fQ.Jud34UpYN1d1L_NSVGvgmLraXmiGf5uTjQI5tIUp9PjeP9UEc70SQcy-WlDX1-hO0P8bxyfuOy48eH0D169CIIEGs4I87qgR8HugQFvzuw4vaZiiDH4JX-By-BuSdm6-NDg5XjS3EHBtGnE3IID7qgAA0DHaPp-cZZ82bsyCGbDm8OhwA6uMPwZSG-oariWULtBGds44iTpXivKC8dutvwVvoKcfVP1FTdB9YHwfULabqZ-PmYkoI8cI_Jd6p97VdzBfin2bg-56s0UxbPeGs5LcYnFgNp-gWOYlNqaeF9QMlsKinnm0wbhWRWgu6EAW-UdwcKeLGN4r9skZ01BtneQaeDNWOGak9119gPU5xHlx8Vu1Cz1VCGzyMdvmcU4SKAq7JYCthUBkTeXSGPO3IzRdgGb_86o3XptObZvDssbeSV-cAhjjz9UiuIVbrkeEZD0frd6GfyRig3NMUdlTQOu9Uj0Pyh85a1cqX_nh-dKV0EFOyXrhIlAr9AHDwAyx6lqfD3tl8bjzgID5bSUsA0ZTmZxWgpiz4BnLF3otBGkHe62hCHZltD8_3G8GxRkC88mwYgPGehlcZH91LZs6lfwTLqn0gejiaAdcJTde5FFVXvJkMd6H07ELwW01w27XyRGRB4wUTdu_2uCzC27rUltu0PYYk3MJs-J_eDAEjpY' \
             --data-raw '{
                           "gameId": "ac1c1781-8bab-4b85-8137-98ab47b0391b",
                           "userId": "aace4689-bb7d-4238-aef8-4d3c530bf374",
                           "row": 0,
                           "column": 1
                        }'
       ```
     Response:

       ```
         {
            "gameId": "635a15d6-528a-4fa1-90fa-5a113a375fad",
            "userId": "cab0de87-aef1-427b-96c9-1b83f30ead74",
            "cellValue": 1,
            "cellsOpened": [
                            {
                              "id": "be07123c-a384-4673-b698-ce523a0603ee",
                              "row": 0,
                              "column": 1,
                              "value": 1,
                              "flagged": false,
                              "opened": true,
                              "detonated": false
                            }
                           ],
            "gameOver": false,
            "won": false
         }
       ```
     * [Flag Cell (PATCH)](http://localhost:8082/api/v1/game/cell/flagged): *Flagged/Unflagged a cell in the playing board.*

       Request:

         ```
          curl --location --request PATCH 'http://localhost:8082/api/v1/game/cell/flagged' \
               --header 'Content-Type: application/json' \
               --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJEcmVhbSBUZWFtIEFQSSIsImV4cCI6MTYzOTc3ODY4NywiaWF0IjoxNjM5Nzc3Nzg3fQ.Jud34UpYN1d1L_NSVGvgmLraXmiGf5uTjQI5tIUp9PjeP9UEc70SQcy-WlDX1-hO0P8bxyfuOy48eH0D169CIIEGs4I87qgR8HugQFvzuw4vaZiiDH4JX-By-BuSdm6-NDg5XjS3EHBtGnE3IID7qgAA0DHaPp-cZZ82bsyCGbDm8OhwA6uMPwZSG-oariWULtBGds44iTpXivKC8dutvwVvoKcfVP1FTdB9YHwfULabqZ-PmYkoI8cI_Jd6p97VdzBfin2bg-56s0UxbPeGs5LcYnFgNp-gWOYlNqaeF9QMlsKinnm0wbhWRWgu6EAW-UdwcKeLGN4r9skZ01BtneQaeDNWOGak9119gPU5xHlx8Vu1Cz1VCGzyMdvmcU4SKAq7JYCthUBkTeXSGPO3IzRdgGb_86o3XptObZvDssbeSV-cAhjjz9UiuIVbrkeEZD0frd6GfyRig3NMUdlTQOu9Uj0Pyh85a1cqX_nh-dKV0EFOyXrhIlAr9AHDwAyx6lqfD3tl8bjzgID5bSUsA0ZTmZxWgpiz4BnLF3otBGkHe62hCHZltD8_3G8GxRkC88mwYgPGehlcZH91LZs6lfwTLqn0gejiaAdcJTde5FFVXvJkMd6H07ELwW01w27XyRGRB4wUTdu_2uCzC27rUltu0PYYk3MJs-J_eDAEjpY' \
               --data-raw '{
                             "gameId": "ac1c1781-8bab-4b85-8137-98ab47b0391b",
                             "userId": "aace4689-bb7d-4238-aef8-4d3c530bf374",
                             "row": 0,
                             "column": 0,
                             "flaggedCell": false
                          }'
         ```
       Response:

         ```
           false
         ```
### API features

* Java/Spring boot maven project.
* In memory DB (H2)
* Spring Oauth2 Authentication/Authorization
* JWT token
* Hateoas support
* Etag support
* [Swagger documentation](http://localhost:8082/v2/api-docs) and [Swagger UI](http://localhost:8082/swagger-ui.html)
* [PostMan collection](postman/Minesweeper.postman_collection.json)

### How to run the API

* From the console line in the root folder of the project execute the following command to generate the .jar file.
  ```
  ./mvnw clean package
  ```
* Now run the application by:

   ```
   ./mvnw spring-boot:run
   ```
* The API by default will be reachable in the following URL *http://localhost:8082*
* *(Optional)* Use [PostMan collection](postman/Minesweeper.postman_collection.json) to hit the endpoints.
 


### Code Coverage
Complete coverage of the following layers:
* Controller
* Service
* Entities
* Exceptions
* Hateoas

![](images/coverage.png)

#### Web version

![](images/coverageReport.png)

### Swagger UI

![](images/swagger-UI.png)

1. Authentication
   ![](images/Swagger-Authentication.png)
2. Game
   ![](images/Swagger-Game.png)

### Game UI - React Front-End

The Minesweeper API has a UI built in React 17, which allow to play the game in a more dynamic and natural way.
The source code can be found in [Minesweeper UI](https://github.com/jiujitsuboy/react-minesweeper) 