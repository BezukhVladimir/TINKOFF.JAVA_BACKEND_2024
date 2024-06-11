### Задача 1
Документация: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html

Добавьте в проект зависимости:
    
    org.springframework.boot:spring-boot-starter-actuator
    io.micrometer:micrometer-registry-prometheus
    io.micrometer:micrometer-tracing-bridge-brave


После этого:
* настройте запуск management endpoint'ов на отдельном порту
* сделайте так, чтобы для доступа к actuator не требовался дополнительный path в URL, т.е. метрики были доступны по /health, а не /actuator/health
* включите info, health и prometheus метрики
* для последнего сделайте так, чтобы метрики отдавались по пути /metrics, а не /prometheus
* добавьте дополнительный лейбл для всех метрик application=${spring.application.name}


Все задачи выше выполняются в application.yaml и не требуют модификации кода.



### Задача 2
Добавьте в compose-файл образы prom/prometheus и grafana/grafana-oss:

Создайте prometheus.yml, укажите в нём target'ы с которых будут собираться метрики (приложения bot и scrapper). Не забудьте, что метрики отдаются на другом порту.

Документация:
* https://prometheus.io/docs/prometheus/latest/getting_started/
* https://prometheus.io/docs/prometheus/latest/configuration/configuration/

Зайдите в Grafana (http://localhost:3000) и добавьте Prometheus как источник данных (DataSource).

Вероятно подойдет следующий URL для Prometheus -- http://localhost:9090 (если вы ничего не меняли).

Проверьте, что на странице http://localhost:3000/explore появились метрики ваших приложений: метрики с префиксом jvm_, tomcat_ и т.п.



### Задача 3
Создайте дашборд (http://localhost:3000/dashboard/new) на основе стандартных метрик:

* количество используемой памяти в единицу времени с разбивкой по типу: jvm_memory_used_bytes
* график скорости gc-аллокаций: jvm_gc_memory_allocated_bytes_total
* среднее время HTTP-ответа: http_server_requests_seconds_sum, http_server_requests_seconds_count

В описании PR пришлите запросы со скриншотами, которые у вас получились.



### Задача 4
Добавьте на дашборд переменную приложения (Dashboard Settings -> Variables).

Сделайте так, чтобы графики показывали не суммарные метрики, а метрики выбранного в приложения.

Пришлите обновленные запросы (или напишите что вы добавили к ним) и скриншот результата.



### Задача 5
Документация: https://micrometer.io/docs/concepts

При помощи библиотеки Micrometer добавьте новую метрику -- количество обработанных сообщений.

Постройте для новой метрики график на борде. Приложите запрос и скриншот результата.

Подсказка: скорее всего вам подойдёт тип Counter.
