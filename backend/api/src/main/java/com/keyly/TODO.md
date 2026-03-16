- Añadir el fix para las updates, no es necesario que pasen las uuid de lo que dependan si no se va a cambiar

- Cambiar put's por patch's

- Comprovación de que un telefono sea un telefono en sucursals

- Darle una vuelta a la relación entre usuarios y baules, quizás deba de ser 1..1

- Darle una vuelta a la tabla compartits en el atributo entitat_uuid, quizás habría que poner la id en vez de la uuid

- Los endpoints de creación múltiple, tratar que pasa si alguno falla en la creación

- Manejar las imagenes de los usuarios

- Gestionar las contraseñas seguras (Una vez el jwt esté hecho)

- Al cambiar la contrasenya, se deberá de volver a encriptar todas las contraseñas del usuario en la bd con la nueva contraseña (JWT hecho)