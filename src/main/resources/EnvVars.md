# Variables de entorno del microservicio

Estas son las variables de entorno que se pueden configurar para este microservicio:

Requeridas:

- **spring.profiles.active**: Ambiente en el cual está corriendo el servicio. Requerido. Valores posibles: dev, staging, production
- **spring.mvc.servlet.path**: Prefijo de las rutas de este microservicio. Requerido. Ejemplo: /api-template/v1

Opcionales:

- **server.port**: Puerto HTTP que utiliza el servidor para exponer los endpoints REST. Predeterminado: 8080.
- **logging.level.com.davivienda**: Nivel de detalle a mostrar en logs. Valores posibles: error, warn, info, debug Predeterminado: info
- **logging.pattern.console**: Pattern para impresion del log.
- **logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter**: Setear en DEBUG para imprimir los request