# Документация по решению 
**Как запустить данное решение**  
Перед запуском решения убедитесь что у вас установлен PostgerSQL и введите данные от postgres
в DatabaseService по пути src/main/resources/application.properties, а именно  
spring.datasource.username=*Имя пользователя  
spring.datasource.password=*Ваш пароль  
spring.datasource.url=*Url где распалагается бд  
Также нужно будет установить:
Magick https://imagemagick.org/index.php  
Для того чтобы запустить данное решение нужно запустить 
1. ServerApplication
2. FilesApplication
3. DatabaseServiceApplication
4. GatewayApplication
5. FrontentApplication
____  
**Описание решения**  
Наше решение использует микросервисную архитектуру.  
Server регистрирует все наши сервисы. Сервис же Files занимается работай с файлами, сервис Database введет работу с бд. Сервис Gateway является нашим шлюзом, который перенаправляет запросы на нужные сервисы.   
Как хранятся фотографии?  
Если фотографии tiff или tif то к ним применятся метод сжатия LZW( позволяет сжимать фотографию без потерь в качестве).  
Если фотографии png то к ним не применятся никакого сжатия ведь png и так уже сжат без потерь


