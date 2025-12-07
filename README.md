Proyecto final de desarrollo de sistemas 3, universidad de sonora. 
Luna Anaya Elias Antonio: Sistema de monitoreo.

Se busca monitorear los datos enviados por un arduino UNO en formato xyz, con el fin de analizar los datos enviados en tiempo real y realizar comparaciones de llegada de datos en fecha y hora especificas.

<img width="949" height="507" alt="image" src="https://github.com/user-attachments/assets/235c72c1-bbd4-4c36-b77a-630d752a7050" />

<img width="971" height="475" alt="image" src="https://github.com/user-attachments/assets/3c068d53-dd89-4758-a22f-b0a572f85792" />

El programa constará de 3 vistas: Vista inicio (para trasladarse a monitor e historico), vista monitor (para mostrar la lectura de datos en tiempo real) y vista historico (en el que podrás revisar los datos leidos con los filtros deseados).

Vista monitor leerá los datos del arduino y estos serán enviados por el cliente al servidor para asi mandarlos a la base de datos. Se utilizarán dos hilos, uno para leer los datos del arduino, y otro para enviar los datos al servidor.

Vista histórico se encargará de recibir del servidor los datos consultados para poder graficarlos en base a los filtros que se deseen.

El servidor es una parte muy importante del programa, pues es el que manda los datos hacia vista histórico para que se puedan graficar, y es el que recibe los datos de vista monitor para poder insertarlos en la base de datos.

¿Qué desafios tuve durante el desarrollo del proyecto?
- Lectura del arduino: Se utilizó un IDE para añadir el código del arduino (para mandar los datos xyz) y se implementó en el código la opción para leer el puerto del arduino y de esta manera leer sus datos.
- Encriptación de datos: Se utilizó la encriptación AES y se creó una clave que servirá para encriptar los datos. El cliente los encriptará, y el servidor los desencriptará
- Problemas dentro del código: Investigación, algunos problemas eran relacionados con la base de datos, con la aplicación de los filtros y el uso de hilos para la lectura en tiempo real.

IMÁGENES DE LA IMPLEMENTACIÓN

<img width="674" height="395" alt="image" src="https://github.com/user-attachments/assets/3c21678a-1d48-4d63-b6a9-94b09d894d23" />

<img width="772" height="400" alt="image" src="https://github.com/user-attachments/assets/2b731c7f-cc20-4f85-a419-ffe2480353b5" />

CONCLUSIONES

A comparación de proyectos y trabajos anteriores en los que he trabajado, el procedimiento y la elaboración de este trabajo ha sido muy extenso y me ha ayudado a comprender temas relacionados con hilos, lectura de arduino, uso de base de datos, entre otros temas que estoy seguro me ayudarán en el futuro para el ámbito laboral. 


Adjunto el enlace de mi presentación en canva: https://www.canva.com/design/DAG6VFPa_C4/i209y_xyV6MaytNkoV_odQ/edit
