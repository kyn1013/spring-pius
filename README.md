## PLUS 주차 도전 기능
### AWS 적용
- healthcheck controller path : /health

- 인스턴스
![Image](https://github.com/user-attachments/assets/ddc8e2b4-9109-4efa-8e82-145404eda0e2)


- 탄력적 ip적용
![Image](https://github.com/user-attachments/assets/11bf661c-66de-407a-b855-57eb6d70d828)
   
- RDS
![Image](https://github.com/user-attachments/assets/6f9b65fd-b5e5-49ff-884e-6bb733349987)

- S3
![Image](https://github.com/user-attachments/assets/72e6b883-ba2d-4571-89b1-ae1085beb03d)

### 대용량 데이터 처리
- User 데이터 100만개 생성

|  | spring data jpa save | jdbctemplate batch insert |
| --- | --- | --- |
| BUILD 완료 시간 | 약 40분 | 1분 28초 |

27.3배 빨라지도록 개선

- nickname으로 일치하는 User를 검색한 경우

|  | index 적용 전 | index 적용 후 |
| --- | --- | --- |
| Average (평균 걸린 시간) | 4872ms | 226ms |

 index 적용 전
![Image](https://github.com/user-attachments/assets/f4a487e6-c0a3-405d-b8b5-5d82f8557621)

 index 적용 후
![Image](https://github.com/user-attachments/assets/7757a2ee-5682-472d-b08e-b175fed04426)

 21.58배 빨라지도록 개선
