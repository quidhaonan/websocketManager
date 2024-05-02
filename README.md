# websocketManager

## 1. 初始化

1. 客户端请求
    + clientId：设备 id，随机字符串

        ```json
        {
            "type": "init",
            "msgId": "",
            "data":{
                "clientId": ""
            }
        }
        ```



## 2. 心跳检测

1. 客户端请求

    ```json
    {
        "type": "heartbeat",
        "msgId": "",
        "data":{
    
        }
    }
    ```



## 3. 通过浏览器进行加密（请求使用 websocket）

1. 浏览器先进行初始化（需要修改：加密函数 e、websocket 地址 url、clientId ）

    ```js
    !(function () {
        if(window.lmy?.flag){
            return
        }

        window.lmy = {
            flag: true
        }
        // e：加密函数
        window.lmy.encrypt = e
        // url：ws地址
        let url = 'ws://localhost:9999/ws'
        let clientId = '123456'

        let ws
        // WebSocket 重连尝试的间隔时间（毫秒）
        const RECONNECT_INTERVAL = 1000;
        // 是否应该尝试重连
        let shouldReconnect = true;

        // 创建 WebSocket 连接函数
        function connect() {
            ws = new WebSocket(url);

            ws.onopen = function (event) {
                let initData = {
                    "type": "init",
                    "msgId": "",
                    "data": {
                        "clientId": clientId
                    }
                }
                ws.send(JSON.stringify(initData))

                setInterval(() => {
                    let heartData = {
                        "type": "heartbeat",
                        "msgId": '',
                        "data": {

                        }
                    }
                    ws.send(JSON.stringify(heartData))
                }, 1000 * 60 * 2)
            }

            ws.onmessage = function (event) {
                let data = {
                    "type": "decrypt",
                    "msgId": JSON.parse(event.data).msgId,
                    "data": {
                        "clientId": clientId,
                        "msg": window.lmy.encrypt(JSON.parse(event.data).data),
                        "flag": true
                    }
                }
                ws.send(JSON.stringify(data))
            }

            ws.onerror = function (error) {
                console.error('WebSocket error:', error);
            };

            ws.onclose = function () {
                console.error('WebSocket disconnected');
                // 等待一段时间后尝试重连
                if (shouldReconnect) {
                    setTimeout(connect, RECONNECT_INTERVAL);
                }
            };
        }

        // 初次连接
        connect();

        // 停止重连
        window.lmy.stopReconnect = function stopReconnect() {
            shouldReconnect = false
        }
    })()
    ```

2. 应用程序对 websocket 进行请求
    + clientId 需与浏览器 clientId 对应
        ```json
        {
            "type": "decrypt",
            "msgId": "",
            "data": {
                "clientId": "",
                "msg": ""
            }
        }
        ```



## 4. 通过浏览器进行加密（请求使用 https）

1. 浏览器先进行初始化（需要修改：加密函数 e、websocket 地址 url、clientId ）

    ```java
    !(function () {
    	if(window.lmy?.flag){
            return
        }
    
        window.lmy = {
            flag: true
        }
        // e：加密函数
        window.lmy.encrypt = e
        // url：ws地址
        let url = 'ws://localhost:9999/ws'
        let clientId = '123456'
    
        let ws
        // WebSocket 重连尝试的间隔时间（毫秒）
        const RECONNECT_INTERVAL = 1000;
        // 是否应该尝试重连
        let shouldReconnect = true;
    
        // 创建 WebSocket 连接函数
        function connect() {
            ws = new WebSocket(url);
    
            ws.onopen = function (event) {
                let initData = {
                    "type": "init",
                    "msgId": "",
                    "data": {
                        "clientId": clientId
                    }
                }
                ws.send(JSON.stringify(initData))
    
                setInterval(() => {
                    let heartData = {
                        "type": "heartbeat",
                        "msgId": '',
                        "data": {
    
                        }
                    }
                    ws.send(JSON.stringify(heartData))
                }, 1000 * 60 * 2)
            }
    
            ws.onmessage = function (event) {
                let data = {
                    "type": "decryptHttps",
                    "msgId": JSON.parse(event.data).data.msgId,
                    "data": {
                        "clientId": clientId,
                        "msg": window.lmy.encrypt(JSON.parse(event.data).data.msg),
                        "flag": true
                    }
                }
                ws.send(JSON.stringify(data))
            }
    
            ws.onerror = function (error) {
                console.error('WebSocket error:', error);
            };
    
            ws.onclose = function () {
                console.error('WebSocket disconnected');
                // 等待一段时间后尝试重连
                if (shouldReconnect) {
                    setTimeout(connect, RECONNECT_INTERVAL);
                }
            };
        }
    
        // 初次连接
        connect();
    
        // 停止重连
        window.lmy.stopReconnect = function stopReconnect() {
            shouldReconnect = false
        }
    })()
    ```

2. 应用程序使用 https 进行请求

   + 地址：localhost:9999/ws/decrypt
   + 将 localhost 切换成服务器 ip，请求方式为 post
   + 参数

       ```json
       {
           "clientId":"",
           "userId":"",
           "msg":""
       }
       ```
   
   + clientId 需与浏览器 clientId 对应，userId 填写唯一即可，msg 待加密文本



## 5. 启动项目

1. 单项目启动（windows 版 docker 为例）

	+ 将 jar 文件与 Dockerfile 文件放在同一级目录
	
	  ```shell
	  docker build -t websocket_manager .
	  ```
	
	+ 创建镜像所需要的网络
	
	  ```shell
	  docker network create  --driver nat network-name
	  ```
	
	+ 运行已创建的镜像
	
	  ```shell
	  docker run -d\
	  --name websocket_manager\
	  -p 9999:9999\
	  -e TZ=Asia/Shanghai\
	  --network lmyxlf\
	  websocket_manager
	  ```
	
	  ```shell
	  docker run  -d  --name websocket_manager -p 9999:9999 -v /c/logs:/logs -e TZ=Asia/Shanghai --network lmyxlf websocket_manager
	  ```
	
	  docker run -d  --name websocket_manager -p 9999:9999  -e TZ=Asia/Shanghai --network lmyxlf websocket_manager





