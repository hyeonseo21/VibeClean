/*
 * esp_mqtt.h
 *
 *  Created on: Nov 10, 2025
 *      Author: khekh
 */

// ESP8266 MQTT 통신 관련 함수들의 선언부이다.

#ifndef INC_ESP_MQTT_H_ // 만약 아직 정의되지 않았다면
#define INC_ESP_MQTT_H_ // 이제 이렇게 정의한다.
// 이 부분은 헤더 가드이다. 이 파일이 여러 번 include되더라도 중복 정의 오류가 발생하지 않게 막아준다.

#include "main.h"

/**
 * 아래의 함수 선언부는 ESP 초기화 및 MQTT 연결을 수행하는 함수이다.
 * @brief ESP8266 + WiFi + MQTT 초기화 및 브로커 연결, 구독까지 수행
 * 리턴 값이 1 성공, 0 실패
 */
int ESP_MQTT_Init(void);

/**
 * 아래의 함수는 STM-> 서버로 Telemetry 데이터를 MQTT로 보내는 함수이다.
 * @brief Telemetry 메시지 MQTT Publish
 * @param floor     현재 바닥 상태 문자열 (예: "CARPET", "HARD")
 * @param fanSpeed  현재 팬 속도
 * @param x         로봇 위치 X
 * @param y         로봇 위치 Y
 */
void MQTT_PublishTelemetry(const char* floor, int fanSpeed,
        int posX, int posY,
        float senX, float senY, float senZ);

/**
 * 이 함수 선언부는 서버->STM으로 오는 제어 명령을 처리하는 함수이다.
 * @brief 수신된 MQTT 제어 메시지 처리
 *        (전원 ON/OFF, 팬 속도 변경 등)
 *        UART 수신 버퍼를 검사해 +MQTTSUBRECV 이벤트를 파싱
 */
void MQTT_ProcessIncoming(void);

/**
 * 이 아래 함수들은, 수동조작 API에서 MQTT로 값 넘겨줌녀 이 함수 안에서 전역변수로 저장하고 다른 데에서 이 함수 사용해서
 * 참고할 수 있도록 함
 * @brief 현재 전원/팬 상태 조회용 (필요시 사용)
 */
int  MQTT_GetPowerState(void);
int  MQTT_GetFanSpeed(void);

#endif /* INC_ESP_MQTT_H_ */
