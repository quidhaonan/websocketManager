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



## 3. 通过浏览器进行加密

1. 浏览器先进行初始化（加密函数、websocket 地址、clientId ）

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
        let url = ''
        let clientId = ''

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

## 4. 启动项目
1. 单文件

```shell
    docker build -t websocket_manager .
```

```
docker run -d \
	--name websocket_manager \
	-p 9999:9999 \
	-e TZ=Asia/Shanghai \
	--network lmyxlf \
	websocket_manager
```

docker run -d  --name websocket_manager -p 9999:9999 -e TZ=Asia/Shanghai --network lmyxlf websocket_manager

```shell
docker network create  --driver nat network-name
```

