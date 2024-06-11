### Задача 1
Напишите bot.Dockerfile и scrapper.Dockerfile и включите сборку образов в стадию build после mvn package.



### Задача 2
Сделайте публикацию получившихся образов в GitHub Container Registry: https://docs.github.com/en/actions/publishing-packages/publishing-docker-images

Публикация происходит из job'ы build.

Как итог, на странице https://github.com/<LOGIN>?tab=packages&repo_name=<REPO_NAME> должны появиться docker-образы с приложениями bot и scrapper.
