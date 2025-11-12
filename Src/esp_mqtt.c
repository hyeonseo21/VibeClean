/*
 * esp_mqtt.c
 *
 *  Created on: Nov 10, 2025
 *      Author: khekh
 */

#include "esp_mqtt.h" // 이 함수의 선언부 적힌 헤더파일
#include "usart.h" // huart2와 같은 UART 핸들러 사용을 위함
#include "string.h" // 문자열 처리용
#include "stdio.h"

/* ====== 사용자 환경에 맞게 수정 ====== */
#define ESP_UART        (&huart2) // ESP8266이 연결된UART 현재 usart2이다.

// 현재 테스트를 위해 내 핫스팟을 기준으로 작성함
#define WIFI_SSID       "Hshshs" // 얘네는 이제 접속할 AP이다. // 여기에는 wifi 이름 적기
#define WIFI_PASS       "seo212121" // wifi 모듈용 의미 //여기에는 wifi 비번 적기

#define MQTT_BROKER_IP  "172.17.67.142" //mosquitto 브로커 정보용
#define MQTT_PORT       1883

#define MQTT_CLIENT_ID  "vibeclean-client(STM)" // mqtt에 접속할 클라이언트 이름
/* ==================================== */

static uint8_t rxBuf[512];
//UART로부터 받은 데이터 임시 저장용 버퍼

/* publish한 수동 조작 상태 subscribe해서 담는 변수. 로봇 상태 (제어 명령 결과를 반영하는 전역 상태) */
static int g_powerOn  = 1; // 수동 조작으로 모드를 조절하는 애다. 이 값이 1이면 on 값이 들어온 거고 이 값이 0이면 off가 들어온 거다.
// 만약 나중에 수동 조작이 일시멈춤 조작 이런 거로 바뀌게 된다면 이 부분 값 역시 조절해야 한다.
static int g_fanSpeed = 1;// 수동 조작으로 팬 속도를 조절하는 애다.

/* ----------------------------------------
 * 내부 유틸 함수
 * ----------------------------------------*/

/**
 * @brief ESP8266에게 AT 명령 전송
 */
static HAL_StatusTypeDef ESP_Send(const char *cmd) // 이 인자에는 "AT\r\n" 등 wifi모듈 설정 위한 AT 명령어 문자열 들어감
{
    return HAL_UART_Transmit(ESP_UART, (uint8_t*)cmd, strlen(cmd), 1000);
    // 인자로 받은 문자열을 UART로 그대로 내보냄.
    // 맨 마지막 매개변수는 타임아웃을 의미함.
    // 맨 처음은 앞에서 &huart2로 미리 define 해둠.
}


/**
 * 정해진 시간 안에 ESP가 OK라고 답했는지 확인하는 함수
 * @brief target 문자열이 나올 때까지 UART 수신 대기
 * @param target   찾을 문자열 (예: "OK", "WIFI GOT IP")
 * @param timeout  전체 대기 시간(ms)
 * @retval 1 찾음, 0 타임아웃/실패
 */
static int ESP_WaitFor(const char *target, uint32_t timeout)
{
    uint32_t start = HAL_GetTick(); // 시작 시간 기록
    uint16_t idx = 0;
    memset(rxBuf, 0, sizeof(rxBuf)); //임시 저장용 버퍼 초기화

    while ((HAL_GetTick() - start) < timeout && idx < sizeof(rxBuf) - 1)
    { // 전체 경과 시간이 timeout보다 작고, 버퍼가 가득차기 전까지 반복됨
        uint8_t ch;
        if (HAL_UART_Receive(ESP_UART, &ch, 1, 20) == HAL_OK)
        { //wifi에게 받는 값은 1바이씩 UART 수신받는데, 최대 20초까지 대기하고 성공 시 ch에 값 넣음
            rxBuf[idx++] = ch;
            rxBuf[idx] = '\0';

            if (strstr((char*)rxBuf, target) != NULL)
            	// 지금까지 받은 문자열 안에 target이 포함되면 1 즉 성공
            {
                return 1;
            }
        }
    }
    return 0;
}

/* ----------------------------------------
 * 초기화: AT → WiFi → MQTT → SUB
 * ----------------------------------------*/
// 이 아래 함수는 맨 처음에 한 번 ESP를 초기화하는 그런 함수

int ESP_MQTT_Init(void)
{
    char cmd[256];

    /* 1) 모듈 응답 확인, wifi모듈이 살아있는지 확인용 OK오면 정상 */
    ESP_Send("AT\r\n");
    if (!ESP_WaitFor("OK", 1000)) return 0;

    /* 2) WiFi STA 모드 설정 */
    ESP_Send("AT+CWMODE=1\r\n"); // wifi 모드를 station 즉 클라이언트로 설정
    if (!ESP_WaitFor("OK", 1000)) return 0;

    /* 3) AP 접속, 즉 우리가 접속하고자 하는 wifi에 접속 */
    snprintf(cmd, sizeof(cmd),
             "AT+CWJAP=\"%s\",\"%s\"\r\n", WIFI_SSID, WIFI_PASS);
    ESP_Send(cmd);
    if (!ESP_WaitFor("WIFI GOT IP", 15000)) return 0; // 이게 오면 IP획득 즉 성공이다.

    /* 4) MQTT 클라이언트 설정: 채널 0 사용 */
    snprintf(cmd, sizeof(cmd),
             "AT+MQTTUSERCFG=0,1,\"%s\",\"\",\"\",0,0,\"\"\r\n",
             MQTT_CLIENT_ID); //MQTT 클라이언트 설정 내가 설정한 id로
    ESP_Send(cmd);
    if (!ESP_WaitFor("OK", 2000)) return 0;

    /* 5) MQTT 브로커 연결 */
    snprintf(cmd, sizeof(cmd),
             "AT+MQTTCONN=0,\"%s\",%d,0\r\n",
             MQTT_BROKER_IP, MQTT_PORT); // 지정한 브로커로 MQTT 연결 시도
    ESP_Send(cmd);
    if (!ESP_WaitFor("OK", 5000)) return 0;

    /* 6) 제어 명령 토픽 구독 (power/speed 모두 #로 처리), 저 control로 시작하는 모든 제어 명령 토픽을 구독함. */
    ESP_Send("AT+MQTTSUB=0,\"vibeclean/robot1/control/#\",0\r\n");
    if (!ESP_WaitFor("OK", 2000)) return 0;

    // 만약 어느 한 단계라도 실패하면 0을 리턴한다.

    return 1;
}

/* ----------------------------------------
 * Telemetry Publish (STM->서버) 센서 값 등 publish
 * ----------------------------------------*/

void MQTT_PublishTelemetry(const char* floor, int fanSpeed,
        int posX, int posY,
        float senX, float senY, float senZ)
{
    char payload[256];
    char cmd[320];

    /* 백엔드 TelemetryMessage JSON 형식에 맞춤 */
    snprintf(payload, sizeof(payload),
             "{\"currentFloor\":\"%s\",\"fanSpeed\":%d,"
             "\"position\":{\"x\":%d,\"y\":%d},\"sensor\":{\"x\":%f,\"y\":%f,\"z\":%f}}",
             floor, fanSpeed, posX, posY,senX,senY,senZ);

    snprintf(cmd, sizeof(cmd),
             "AT+MQTTPUB=0,\"vibeclean/robot1/telemetry\",\"%s\",0,0\r\n",
             payload);

    ESP_Send(cmd);
    ESP_WaitFor("OK", 1000);    /* 단순 확인 (실패시 재전송 로직 등은 추후 보강) */
}

/* ----------------------------------------
 * Subscribe 수신 처리
 *  +MQTTSUBRECV:0,"vibeclean/robot1/control/power or speed","{\"power\":\"ON\"}"
 * ----------------------------------------*/

/* 제어 명령에 대한 현재 상태 ACK Publish */ //제대로 값이 들어왔는지 확인하기 위한 테스트 코드.
static void MQTT_PublishControlAck(void)
{
    char payload[128];
    char cmd[256];

    /* 현재 적용된 상태를 그대로 알려줌 */
    snprintf(payload, sizeof(payload),
             "{\"power\":\"%s\",\"fanSpeed\":%d}",
             g_powerOn ? "ON" : "OFF",
             g_fanSpeed);

    /* 서버(또는 개발 PC)에서 확인용으로 구독할 토픽 */
    snprintf(cmd, sizeof(cmd),
             "AT+MQTTPUB=0,\"vibeclean/robot1/ack/control\",\"%s\",0,0\r\n",
             payload);

    ESP_Send(cmd);
    ESP_WaitFor("OK", 1000);  // 실패 시 재전송 로직은 필요시 추가
}

//server쪽에서 받은 거 처리하는 함수
void MQTT_ProcessIncoming(void)
{
	static uint16_t idx = 0;   // rxBuf에 얼마나 채웠는지 기억
	uint8_t ch;

	/* ESP_WaitFor 등에서 rxBuf를 비웠으면 idx도 같이 리셋 */
	if (rxBuf[0] == '\0' && idx != 0) {
	        idx = 0;
	    }

	/* 1) UART에서 들어온 데이터 rxBuf에 누적 */
	while (idx < sizeof(rxBuf) - 1 &&HAL_UART_Receive(ESP_UART, &ch, 1, 5) == HAL_OK)
	{
	      rxBuf[idx++] = ch;
	      rxBuf[idx] = '\0';
	}

    char *p = strstr((char*)rxBuf, "+MQTTSUBRECV"); //publish된 MQTT 메세지 존재히먄 시작함.
    if (!p) return;

    /* 토픽 파싱 */
    char *topicStart = strchr(p, '\"');
    if (!topicStart) goto CLEAR;
    char *topicEnd = strchr(topicStart + 1, '\"');
    if (!topicEnd) goto CLEAR;

    char topic[64] = {0};
    strncpy(topic, topicStart + 1, topicEnd - topicStart - 1);

    /* payload(Json 문자열) 파싱 */
    char *payloadStart = strchr(topicEnd + 1, '\"');
    if (!payloadStart) goto CLEAR;
    char *payloadEnd = strrchr(payloadStart + 1, '\"');
    if (!payloadEnd) goto CLEAR;

    char payload[128] = {0};
    strncpy(payload, payloadStart + 1, payloadEnd - payloadStart - 1);

    /* 토픽별 처리 */
    if (strstr(topic, "vibeclean/robot1/control/power"))
    {
        if (strstr(payload, "ON")) //on 신호를 받은 경우 변수에 1 저장
        {
            g_powerOn = 1;
            /* TODO: 실제 모터/전원 ON 제어 코드 */
        }
        else
        {
            g_powerOn = 0; //off 신호면 변수에 0저장
            /* TODO: 실제 모터/전원 OFF 제어 코드 */
        }
        MQTT_PublishControlAck(); // 값이 잘 들어왔는지 확인하기 위해 테스트 토픽으로 보냄
    }
    else if (strstr(topic, "vibeclean/robot1/control/speed"))
    {
        int newSpeed = 0;
        if (sscanf(payload, "{\"fanSpeed\":%d}", &newSpeed) == 1)
        {
            g_fanSpeed = newSpeed; // fanSpeed값 받으면 변수에 저장
            /* TODO: PWM 제어 등으로 실제 팬 속도 변경 */

            MQTT_PublishControlAck(); // 값이 잘 들어왔는지 확인하기 위해 테스트 토픽으로 보냄
        }
    }

CLEAR:
    memset(rxBuf, 0, sizeof(rxBuf)); // 다 처리 후 rxbuf를 초기화한다.
    idx=0;
}

/* ----------------------------------------
 * 상태 조회 (필요시 사용)
 * ----------------------------------------*/

// 서버에서 보낸 수동 조작 값들을 sebscribe 하면 변수들에 저장하는데 해당 변수들의 getter함수이다.
int MQTT_GetPowerState(void)
{
    return g_powerOn;
}

int MQTT_GetFanSpeed(void)
{
    return g_fanSpeed;
}


