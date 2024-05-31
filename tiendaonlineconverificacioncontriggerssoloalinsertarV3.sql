drop database if exists base_datos_tienda_con_triggers_al_insertar_v3;
create database base_datos_tienda_con_triggers_al_insertar_v3;
use base_datos_tienda_con_triggers_al_insertar_v3;

CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    imagen_url VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE fabricantes (
    id_fabricante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
	fecha_fundacion DATE,
    pais VARCHAR(50),
    pagina_web VARCHAR(50),
    imagen_url VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN,
    nombre VARCHAR(30) NOT NULL,
    apellido1 VARCHAR(40) NOT NULL,
    apellido2 VARCHAR(40),
    role ENUM('ROLE_ADMIN', 'ROLE_USER') NOT NULL DEFAULT 'ROLE_USER',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE verificacion_uuid (
    id_verificacion_uuid BIGINT AUTO_INCREMENT PRIMARY KEY,
    identidad_usuario INT,
    uuid VARCHAR(255) NOT NULL,
    fecha_expiracion TIMESTAMP NOT NULL,
    FOREIGN KEY (identidad_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE TABLE subcategorias (
    id_subcategoria INT AUTO_INCREMENT PRIMARY KEY,
    identidad_categoria INT,
    nombre VARCHAR(50),
    imagen_url VARCHAR(100) NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (identidad_categoria) REFERENCES categorias(id_categoria)
);

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    identidad_fabricante INT NOT NULL,
    identidad_subcategoria INT NOT NULL,
    nombre VARCHAR(75) UNIQUE,
    descripcion TEXT,                                    
    detalles VARCHAR(100),
    precio DECIMAL(7,2),
    stock INT UNSIGNED,
    novedad BOOLEAN,
    tipo_descuento ENUM('sin_descuento', 'porcentual', 'absoluto') NOT NULL DEFAULT 'sin_descuento',
    descuento DECIMAL(5,2) DEFAULT NULL,
    /*Precio después de aplicar el descuento, caso de que existe*/
    precio_final DECIMAL(7, 2),
	numero_valoraciones INT NOT NULL DEFAULT 0,
    valoracion_media DOUBLE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (identidad_fabricante) REFERENCES fabricantes(id_fabricante),
    FOREIGN KEY (identidad_subcategoria) REFERENCES subcategorias(id_subcategoria)
);

CREATE TABLE imagenes (
    id_imagen INT AUTO_INCREMENT PRIMARY KEY,
    identidad_producto INT NOT NULL,
    imagen_url VARCHAR(100),
    FOREIGN KEY (identidad_producto) REFERENCES productos(id_producto)
);

CREATE TABLE resenas (
    id_resena INT AUTO_INCREMENT PRIMARY KEY,
    identidad_producto INT NOT NULL,
    identidad_usuario INT NOT NULL,
    valoracion INT,
    titulo VARCHAR(100),
    comentario TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (identidad_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (identidad_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    identidad_usuario INT,
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    /*precio_total se calculará será la suma de los precio_linea de la linea_facturacion para el id_pedido en cuestión*/
	precio_subtotal DECIMAL(8, 2),
    metodo_envio ENUM('Recogida_en_tienda', 'CTT_Express', 'NACEX') NOT NULL DEFAULT 'CTT_Express',
    gastos_envio DECIMAL(4, 2), /*gastos_envio depende de precio_subtotal y metodo_envio*/
    precio_total DECIMAL(8, 2),
    estado ENUM('pendiente', 'enviado', 'entregado', 'cancelado') DEFAULT 'pendiente',
    /*Datos del destinatario*/
	nombre VARCHAR(30) NOT NULL,
    apellidos VARCHAR(40) NOT NULL,
    direccion VARCHAR(80) NOT NULL,
    pais VARCHAR(40),
    ciudad VARCHAR(30),
    numero_telefono_movil VARCHAR(20),
    /**********/
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (identidad_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE linea_facturacion (
    id_linea_facturacion INT AUTO_INCREMENT PRIMARY KEY,
    identidad_pedido INT,
    identidad_producto INT,
    cantidad INT NOT NULL CHECK (cantidad >= 1),
    estado ENUM('activo', 'cancelado') DEFAULT 'activo',
    /*El precio_linea = precio del producto*cantidad*/
    precio_unitario DECIMAL(7, 2) NOT NULL,
    precio_linea DECIMAL(7, 2) NOT NULL,
    UNIQUE (identidad_pedido, identidad_producto),
    FOREIGN KEY (identidad_pedido) REFERENCES pedidos(id_pedido),
    FOREIGN KEY (identidad_producto) REFERENCES productos(id_producto)
);

CREATE TABLE envios (
    id_envio INT AUTO_INCREMENT PRIMARY KEY,
    identidad_pedido INT, /*Consideramos que no es UNIQUE para permitir más de un envío por pedido (por haber resultado los anteriores fallidos o estar su contenido incompleto);*/
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega DATETIME,
    numero_documento_identidad_receptor VARCHAR(20),
    fecha_entrega_vuelta_almacen DATETIME,
    comentario VARCHAR(150), /*P. ej. si ha habido varios intentos de entrega*/
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (identidad_pedido) REFERENCES pedidos(id_pedido)
);

/*Tabla productos: Al insertar un nuevo producto, a partir de precio y descuento se calcula su precio_final*/
DELIMITER $$
CREATE TRIGGER before_insert_productos
BEFORE INSERT ON productos
FOR EACH ROW
BEGIN
    IF NEW.tipo_descuento = 'porcentual' THEN
        SET NEW.precio_final = NEW.precio - (NEW.precio * NEW.descuento / 100);
    ELSEIF NEW.tipo_descuento = 'absoluto' THEN
        SET NEW.precio_final = NEW.precio - NEW.descuento;
    ELSE
        SET NEW.precio_final = NEW.precio;
    END IF;
END$$
DELIMITER ;

/*Tabla productos: Al insertar una nueva reseña se recalcula la valoracion_media del producto al que hace referencia*/
DELIMITER $$
CREATE TRIGGER actualizar_valoracion_media_after_insert
AFTER INSERT ON resenas
FOR EACH ROW
BEGIN
    -- Actualiza el número de valoraciones y recalcula la valoración media
    UPDATE productos
    SET numero_valoraciones = numero_valoraciones + 1,
	valoracion_media = IF(numero_valoraciones = 1, NEW.valoracion,
                      (valoracion_media * (numero_valoraciones - 1) + NEW.valoracion) / numero_valoraciones)
    WHERE id_producto = NEW.identidad_producto;
END$$
DELIMITER ;

/*Tabla linea_facturacion: al insertar una nueva línea de facturación se calcula automáticamente precio_linea*/
DELIMITER $$
CREATE TRIGGER before_insert_linea_facturacion
BEFORE INSERT ON linea_facturacion
FOR EACH ROW
BEGIN
    -- Obtener el precio final del producto relacionado
    SELECT precio_final INTO @precio_unitario
    FROM productos
    WHERE id_producto = NEW.identidad_producto;
    -- Establecer precio unitario y calcular precio línea
    SET NEW.precio_unitario = @precio_unitario;
    SET NEW.precio_linea = NEW.precio_unitario * NEW.cantidad;
END$$
DELIMITER ;

/*Tabla productos: Insertar una linea_facturacion implica una actualización del stock de ese producto*/
DELIMITER $$
CREATE TRIGGER after_insert_linea_facturacion_reduce_stock
AFTER INSERT ON linea_facturacion
FOR EACH ROW
BEGIN
    UPDATE productos
    SET stock = stock - NEW.cantidad
    WHERE id_producto = NEW.identidad_producto;
END$$
DELIMITER ;

/*Tabla pedidos: al insertar un registro en la tabla linea_facturacion: recálculo de precio_subtotal*/
DELIMITER $$
CREATE TRIGGER after_lineafacturacion_insert
AFTER INSERT ON linea_facturacion
FOR EACH ROW
BEGIN
    UPDATE pedidos
    SET precio_subtotal = (SELECT SUM(precio_linea) FROM linea_facturacion WHERE identidad_pedido = NEW.identidad_pedido)
    WHERE id_pedido = NEW.identidad_pedido;
    
    UPDATE pedidos
    SET gastos_envio = CASE
                            WHEN metodo_envio = 'Recogida_en_tienda' THEN 0
                            WHEN metodo_envio = 'CTT_Express' AND precio_subtotal < 80 THEN 4.95
                            WHEN metodo_envio = 'CTT_Express' AND precio_subtotal >= 80 THEN 0
                            WHEN metodo_envio = 'NACEX' AND precio_subtotal < 80 THEN 5.99
                            WHEN metodo_envio = 'NACEX' AND precio_subtotal >= 80 THEN 3.60
                            ELSE 0 -- default
                       END
    WHERE id_pedido = NEW.identidad_pedido;

    UPDATE pedidos
    SET precio_total = precio_subtotal + gastos_envio
    WHERE id_pedido = NEW.identidad_pedido;

END$$
DELIMITER ;

/*Tabla pedidos. Al pasar a estar un pedido enviado el campo estado de ese pedido pasa a ser enviado*/
DELIMITER $$
CREATE TRIGGER update_pedido_status_after_envio
AFTER INSERT ON envios
FOR EACH ROW
BEGIN
    UPDATE pedidos
    SET estado = 'enviado'
    WHERE id_pedido = NEW.identidad_pedido;
END$$
DELIMITER ;

/*Normalmente, MySQL no permite que un trigger actualice la misma tabla que activó el trigger para evitar la recursión*/
/*DELIMITER $$
CREATE TRIGGER update_pedido_status_to_entregado_on_update2
BEFORE INSERT ON envios
FOR EACH ROW
BEGIN
    -- Obtener el precio final del producto relacionado
    SELECT precio_final INTO @precio_unitario
    FROM productos
    WHERE id_producto = NEW.identidad_producto;
    -- Establecer precio unitario y calcular precio línea
    SET NEW.precio_unitario = @precio_unitario;
    SET NEW.precio_linea = NEW.precio_unitario * NEW.cantidad;
END$$
DELIMITER ;*/

INSERT INTO categorias(nombre, imagen_url, descripcion) value
    ('Armas airsoft', 
    '/imagenes/categorias/replicas.jpg',
    'Las armas de airsoft son réplicas de armas utilizadas en los deportes de airsoft. Son un tipo especial de armas de aire de cañón liso de baja potencia diseñadas para disparar proyectiles llamados "airsoft pellets" o BBs, los cuales están típicamente hechos de (pero no limitados a) materiales plásticos o resinas biodegradables. Las plantas de energía de las armas de airsoft están diseñadas para tener bajas calificaciones de energía en la boca del cañón (generalmente menos de 1.5 J, o 1.1 ft⋅lb) y los pellets tienen significativamente menos poder penetrante y de detención que las armas de aire convencionales, y son generalmente seguras para uso deportivo competitivo y recreativo si se lleva el equipo de protección adecuado.'),
    ('Munición y recarga', 
    '/imagenes/categorias/consumibles.jpg',
    'p>La munición se utiliza para marcar a otros jugadores durante el juego. Estas son pequeñas esferas de plástico, conocidas como BBs, que se disparan desde las réplicas de armas. El objetivo principal es alcanzar a los oponentes con estas BBs para "eliminarlos" o cumplir objetivos específicos dentro del escenario del juego. Las BBs pueden ser de diferentes pesos y calidades, y su elección puede influir en la precisión y el rendimiento de la réplica.</p>
    <p>La recarga en airsoft implica rellenar los cargadores de las réplicas con estas BBs. Los jugadores deben cargar manualmente sus cargadores, ya sea utilizando un tubo cargador o un dispositivo de carga rápida. Esta acción es más que una simple tarea; es una habilidad táctica crucial en el juego. Recargar requiere estrategia y buen timing, ya que hacerlo en un momento inapropiado o en una posición expuesta puede dejar al jugador vulnerable a ser marcado por los oponentes.</p>
    <p>Por lo tanto, saber cuándo y dónde recargar forma parte integral de la táctica y la estrategia en el airsoft, añadiendo un elemento de realismo y desafío que mejora la experiencia del juego.</p>'),
    ('Accesorios',
    '/imagenes/categorias/accesorios.jpg',
	'<p>Los accesorios son elementos adicionales que se pueden añadir a las réplicas de armas para mejorar su funcionalidad, eficiencia, comodidad y realismo. Estos accesorios no solo sirven para mejorar el rendimiento en el campo de batalla, sino también para aumentar la inmersión en el juego y hacer que la experiencia más agradable y personalizada.</p>'),
    ('Equipamiento',
    '/imagenes/categorias/equipamiento.jpg',
    '<p>El equipamiento en Airsoft es fundamental para asegurar la seguridad y la efectividad durante el juego. Este incluye réplicas de armas como rifles, pistolas, escopetas y francotiradores, que disparan bolas de plástico o biodegradables. La protección personal es crucial, destacando el uso de máscaras y gafas para proteger los ojos y la cara, cascos para la cabeza, chalecos tácticos que ofrecen protección y espacio para llevar accesorios, y guantes para proteger las manos y mejorar el agarre. La vestimenta también es importante; se utilizan uniformes, a menudo de camuflaje, para integrarse con el entorno y botas tácticas para proteger los pies en terrenos irregulares.</p>
    <p>Los accesorios para armas como mirillas, visores, silenciadores, linternas y láseres mejoran la funcionalidad y precisión. La comunicación entre jugadores se facilita mediante radios y auriculares, y la hidratación se mantiene con mochilas de hidratación o cantimploras, especialmente vital en juegos largos o en climas cálidos. Todo este equipamiento no solo enriquece la experiencia de juego haciéndola más inmersiva y estratégica, sino que también es esencial para la protección de los jugadores.</p>'),
    ('Internos',
    '/imagenes/categorias/internos.jpg',
    '<p>Los "elementos internos" se refieren a las partes y componentes que están dentro de la réplica de arma y que son esenciales para su funcionamiento. Estos incluyen varios sistemas y piezas que influyen en el rendimiento del arma, como la precisión, la potencia y la fiabilidad.</p>');

INSERT INTO fabricantes(nombre, pais, pagina_web, imagen_url, descripcion) VALUES
    ('Ares Amoeba', 'China', 'https://www.amoeba-airsoft.com/', 'ares-amoeba.jpg', 
    '<p>Ares Amoeba es una marca de réplicas de airsoft reconocida por su innovación y calidad. La marca se enfoca en ofrecer productos de alto rendimiento y durabilidad a precios accesibles para jugadores de todos los niveles.</p>
    <p>Una de las principales características de las réplicas Ares Amoeba es su sistema de gearbox modular, que permite una fácil personalización y actualización de la réplica. Este sistema modular también hace que las réplicas Ares Amoeba sean fáciles de mantener y reparar.</p>
    <p>Otra característica distintiva de las réplicas Ares Amoeba es su diseño ergonómico y estilizado, que las hace cómodas y fáciles de manejar durante las partidas de airsoft. Además, muchas de las réplicas Ares Amoeba vienen con características avanzadas como miras ópticas, guardamanos Keymod y sistemas de gatillo electrónico.</p>
    <p>Ares Amoeba también se destaca por su compromiso con la innovación y el desarrollo constante de nuevas tecnologías para mejorar la experiencia de juego de los jugadores de airsoft.</p>
    <p>En resumen, Ares Amoeba es una marca de réplicas de airsoft con una sólida reputación por su innovación, calidad y diseño ergonómico. Si buscas una réplica de airsoft que ofrezca alto rendimiento y personalización, Ares Amoeba es una excelente opción a considerar.</p>
    <p>En nuestra tienda online Nombre Tienda tienes a tu disposición el catálogo completo de Ares Amoeba</p>'),
    ('BO Manufacture', 'Francia', 'https://bomanufacture.com/', 'bo-manufacture.jpg',
    '<p>BO Manufacture es una empresa nacida y afincada en Francia. Desde sus inicios hace más de 20 años se especializaron, sobre todo, en réplicas de armas famosas de airsoft.</p>
    <p>No obstante, y dado el éxito de la compañía, fueron ampliando su mercado con el diseño de otros productos para airsoft y para outdoor. Pronto, diversos accesorios, munición, cargadores, carabinas e incluso ampliaciones, complementos y partes separadas fueron apareciendo en su línea de producción para dar al experto y aficionado al tiro deportivo una experiencia única y de enorme calidad.</p>
    <p>Esta marca no ha dejado de crecer con el paso del tiempo. Además de ser una gran diseñadora, y más allá de comercializar sus productos en todo el mundo, también ha creado otras líneas que, probablemente, te suenen bastante. Junto a BO Manufacture, se unen los nombres de BO Dynamics y Black Ops, con lo que cubren todo el mercado de necesidades de los aficionados y los expertos y profesionales del airsoft.</p>'),
    ('DBOYS', 'N/A', 'https://dboysguns.com/', 'dboys.jpg',
    '<p>DBOYS es una de las primeras marcas que aparecieron en Airsoft, ofreciendo unos productos de gama media a unos precios imbatibles, poco a poco se a convertido en una de las marcas mas vendidas, pero desde hace unos años Dboys no se oía en el mercado. Según nos cuentan la empresa a sido traspasada, y sus nuevos directivos han decidido en apostar fuerte, manteniendo productos a precios imbatibles, pero ofreciendo un producto de mayor calidad, adaptándose al mercado de hoy en día. Han incorporado nuevos modelos y nuevos Gearboxs.</p>
    <p>El fabricante DBOYS nos presenta sus nuevos modelos de armas de Airsoft AEG con su nuevo Gearbox Gen 2. Nos a sorprendido con un gearbox mas reforzado que sus versiones anteriores, cuenta con pistón con corredera metálica completa así como con un nuevo sistema de cambio rápido de muelle directamente desde la culata, el cual nos facilita mucho el cambio de potencia en nuestras AEG. Esto nos a sorprendido tanto que estamos orgullosos de poder disponer de un amplio catalogo en los nuevos modelos de DBOYS Airsoft en nuestra tienda online.</p>
    <p>A diferencia de otras marcas, DBOYS nos a sorprendido con su nuevo Gearbox reforzado, ofreciendo replicas a unos precios imbatibles y ofreciendo un Gearbox que va a ser una excelente base para poder realizar cualquier futuro Upgrade.</p>
    <p>Gracias a sus mejoras, pensamos que estas replicas van a dar mucho que hablar entre los nuevos jugadores que deseen comenzar en el Airsoft por muy poco dinero, y obtener una base perfecta para poder ir mejorando poco a poco.</p>'),
    ('Duel Code', 'España', 'N/A', 'duel-code.jpg',
    '<p>DUEL CODE es una marca líder en el mundo del airsoft que se ha ganado su reputación gracias a sus productos de alta calidad y rendimiento excepcional. La marca ofrece una amplia variedad de armas de airsoft, piezas de repuesto y accesorios para satisfacer las necesidades de los jugadores de todos los niveles, desde principiantes hasta profesionales.</p>
    <p>Los productos de Duel Code destacan por su durabilidad, precisión y fiabilidad, lo que los convierte en una elección popular entre los jugadores de airsoft de todo el mundo. Además, la marca se esfuerza por mantenerse al día con las últimas tecnologías y tendencias en el mundo del airsoft, asegurando que sus productos sean innovadores y de vanguardia.</p>
    <p>Si estás buscando productos de airsoft de alta calidad y confiabilidad, Duel Code es la marca ideal para ti. Con su amplia gama de productos y su compromiso con la satisfacción del cliente, Duel Code es una marca que no puedes dejar de tener en cuenta.<p>'),
    ('G&G', 'Taiwán', 'https://www.guay2.com/', 'gg.jpg',
    '<p>G&G Armament es una marca reconocida a nivel mundial por la fabricación de réplicas de airsoft de alta calidad. Sus productos son conocidos por su precisión, fiabilidad y durabilidad, lo que los hace ideales tanto para jugadores experimentados como para aquellos que se están iniciando en el mundo del airsoft.</p>
    <p>La empresa fue fundada en Taiwán en 1986 y desde entonces se ha centrado en la innovación y la mejora continua de sus productos. G&G Armament cuenta con un equipo de ingenieros y diseñadores altamente capacitados que trabajan constantemente en el desarrollo de nuevas tecnologías y materiales para mejorar el rendimiento y la calidad de sus productos.</p>
    <p>Entre los productos más populares de G&G Armament se encuentran las réplicas de fusiles de asalto, subfusiles y pistolas de airsoft. Estas réplicas están diseñadas para ser lo más fieles posible a las armas reales, tanto en su aspecto como en su funcionamiento.</p>
    <p>Además, G&G Armament se preocupa por la seguridad de sus usuarios y por el cumplimiento de las normativas internacionales. Por esta razón, todos sus productos están fabricados con materiales de alta calidad y cumplen con los estándares de seguridad más exigentes.</p>
    <p>En resumen, G&G Armament es una marca reconocida en el mundo del airsoft por la calidad y precisión de sus réplicas de airsoft. Sus productos son ideales tanto para jugadores experimentados como para aquellos que se están iniciando en este deporte, y están diseñados para cumplir con los estándares de seguridad más exigentes.</p>'),
    ('Krytac', 'EE.UU.', 'https://krytac.com/', 'krytac.jpg',
    '<p>Krytac es una marca de airsoft con sede en California, Estados Unidos. La marca se especializa en la fabricación de réplicas de armas de fuego de alta calidad y rendimiento para el juego de airsoft. Krytac es una subsidiaria de la empresa matriz KRISS USA, que es conocida por su innovación en el diseño de armas de fuego.</p>
    <p>La línea de productos de Krytac incluye rifles de asalto, pistolas de gas y accesorios de airsoft. Sus productos se destacan por su calidad de construcción y su rendimiento superior en el campo de juego. Krytac utiliza tecnología y materiales avanzados en la fabricación de sus productos para garantizar la máxima durabilidad y precisión.</p>
    <p>Además de su enfoque en la calidad, Krytac también se enfoca en la ergonomía del diseño de sus productos. La marca ha ganado varios premios por sus diseños innovadores y ha sido reconocida por su atención al detalle en la fabricación de réplicas de armas de fuego.</p>
    <p>En resumen, Krytac es una marca de airsoft con sede en California que se especializa en la fabricación de réplicas de armas de fuego de alta calidad y rendimiento. Sus productos se destacan por su atención al detalle, diseño ergonómico y rendimiento superior en el campo de juego, y han ganado varios premios por su innovación en la industria del airsoft.</p>
    <p>Fabricante de armas de Airsoft de la mas alta calidad, con internos reforzados. Perfecta para jugadores veteranos que buscan un arma profesional para practicar el deporte.</p>'),
    ('Lancer tactical', 'EE.UU.', 'https://www.lancertactical.com/', 'lancer-tactical.jpg',
    '<p>Lancer Tactical es una marca estadounidense de airsoft que se especializa en la producción de armas de Airsoft y accesorios de alta calidad . La marca se fundó en 2012 y se ha establecido como una de las marcas líderes en la industria de airsoft en los Estados Unidos.</p>
    <p>Lancer Tactical es conocida por su compromiso con la calidad y la atención al detalle en la producción de sus productos. Sus réplicas de airsoft son conocidas por su buena construcción y rendimiento, y son ampliamente utilizadas tanto en partidas de airsoft como en entrenamiento de fuerzas de seguridad.</p>
    <p>Lancer Tactical aparte de su amplia gama de replicas de Airsoft también ofrecen una amplia gama de accesorios personalizados para sus productos, como miras, cargadores y baterías.</p>
    <p>Además, Lancer Tactical se ha comprometido a ser una marca responsable y comprometida con la seguridad en la industria de airsoft. Han trabajado en estrecha colaboración con organizaciones y reguladores para garantizar que sus productos cumplan con los estándares de seguridad y calidad.</p>
    <p>En resumen, Lancer Tactical es una marca estadounidense de airsoft que se enfoca en la producción de réplicas de Airsoft y accesorios de alta calidad para airsoft. Su compromiso con la calidad y la seguridad ha llevado a la marca a ser reconocida como una de las principales marcas de airsoft en los Estados Unidos, desde entonces ha revolucionado el mercado Estadounidense con productos de alta calidad, ofreciendo las mejores garantías en sus armas de airsoft.</p>'),
    ('Nimrod', 'N/A', 'https://nimrodtactical.com/', 'nimrod-tactical.png',
    '<p>Nimrod es una destacada marca en la industria del airsoft, especializada en suministrar una amplia gama de accesorios y equipos de alta calidad para satisfacer las necesidades de los jugadores más exigentes. Con un enfoque en la excelencia y el rendimiento, Nimrod se ha convertido en un nombre confiable en el mundo del airsoft.</p>
    <p>La marca se destaca por su línea de productos que abarca desde gas de calidad premium, diseñado para maximizar el rendimiento de las réplicas de airsoft y asegurar una operación suave y consistente, hasta bolas de alta precisión que garantizan una trayectoria estable y confiable en el campo de juego. Además, Nimrod ofrece baterías de última generación que proporcionan la energía necesaria para mantener el funcionamiento óptimo de las réplicas eléctricas, así como motores de alto rendimiento que permiten un mejor control y respuesta en el disparo.</p>
    <p>Los cronógrafos de Nimrod son herramientas esenciales para medir la velocidad de las bolas y asegurar que las réplicas cumplan con las regulaciones de campo, garantizando un juego seguro y justo. Las miras de precisión ofrecidas por la marca brindan a los jugadores una ventaja adicional al mejorar la puntería y la adquisición de objetivos. Además, los trazadores de Nimrod añaden un elemento de diversión y realismo al permitir que las bolas emitan una estela de luz en la oscuridad, lo que agrega un toque único a las partidas nocturnas.</p>'),
    ('Saigo Defense', 'España', 'N/A', 'saigo-defense.jpg',
    '<p>Saigo Defense es una marca de airsoft con sede en España que se especializa en la producción de armas de airsoft. La marca se fundó en 2010 y se ha establecido como una de las principales marcas de airsoft en España.</p>
    <p>Las replicas de airsoft de la marca Saigo Defense están diseñadas para jugadores que buscan calidad precio en sus productos. Gracias a Saigo Defense podemos ofrecer estos productos ofreciendo las mejores garantías post-venta. </p>
    <p>Contamos con una amplia gama de productos, el abanico que nos ofrece la marca Saigo Defense cuenta con escopetas, pistolas de gas, pistolas de muelle, así como rifles de Airsoft. </p>'),
    ('Tokyo Marui', 'Japón', 'https://www.tokyo-marui.co.jp/', 'tokyo-marui.jpg',
    '<p>Tokyo Marui es una marca líder en el mundo del airsoft y es reconocida por la calidad y precisión de sus réplicas de armas. Fundada en 1965 en Japón, la marca se ha especializado en la creación de réplicas de armas de fuego para el mercado del airsoft. Tokyo Marui utiliza tecnología avanzada en la fabricación de sus productos, lo que les permite ofrecer réplicas de armas precisas y de alta calidad. </p>
    <p>Una de las características más destacadas de las réplicas de armas de Tokyo Marui es su sistema de hop-up ajustable, que permite una mayor precisión en los disparos a larga distancia. Además, la marca utiliza materiales de alta calidad en la fabricación de sus productos, lo que los hace duraderos y resistentes. Tokyo Marui ofrece una amplia variedad de modelos de réplicas de armas, desde pistolas hasta francotirador, para satisfacer las necesidades y gustos de los jugadores de airsoft más exigentes.</p>
    <p>Otro aspecto que distingue a Tokyo Marui es su compromiso con la seguridad y la responsabilidad social. La marca ha desarrollado réplicas de armas que son seguras de usar y no causan daño a las personas o al medio ambiente.</p>
    <p>En resumen, Tokyo Marui es una marca de réplicas de armas de airsoft de alta calidad y precisión que utiliza tecnología avanzada y materiales de alta calidad en la fabricación de sus productos. La marca se compromete con la seguridad y la responsabilidad social, lo que la convierte en una opción popular para los jugadores de airsoft de todo el mundo.</p>');

INSERT INTO subcategorias(identidad_categoria, nombre, imagen_url, descripcion) VALUES
    (1, 'fusiles', '/imagenes/subcategorias/fusiles.jpg', 
    '<p>Los fusiles de airsoft son la elección ideal para todo tipo de partidas de airsoft, ya que son las armas más versátiles, convirtiéndose en una de las armas más utilizadas por los jugadores de airsoft</p>'),
    (1, 'subfusiles', '/imagenes/subcategorias/subfusiles.jpg', 
    '<p>Los subfusiles de airsoft son armas muy utilizadas por los jugadores de airsoft que buscan juegos rápidos a cortas distancias, estos utilizan cargadores mas estrechos que los fusiles, convirtiéndolas en replicas exactas de los subfusiles reales, los cuales están diseñados para utilizarse con munición de pistola.</p>
    <p>El subfusil de airsoft es la elección perfecta para los amantes del CQB o partidas de corto alcance. A pesar de que estas armas también pueden llegar a distancias mas largas pudiendo casi igualar a los fusiles, generalmente están diseñadas con cañones mas cortos para facilitar su movilidad y convertirlas en armas mas rápidas.</p>'),
    (1, 'pistolas', '/imagenes/subcategorias/pistolas.jpg', 
    '<p>Si quieres iniciarte en este hobby o renovar tu equipamiento por muy poco dinero, una pistola airsoft es el producto ideal para empezar con buen pie o salir victorioso de todos tus enfrentamientos a corta distancia.</p>'),
    (1, 'escopetas', '/imagenes/subcategorias/escopetas.jpg', 
    '<p>Las escopetas Airsoft son una de las réplicas Airsoft más usadas por los jugadores a nivel mundial después de los rifles y las pistolas eléctricas. Estas escopetas de bolas son ideales para la simulación militar de un combate ordinario y también para aquellos que son aficionados a las actividades de enfrentamiento.</p> 
    <p>Por lo general, son empleadas como una herramienta de defensa en contra del enemigo que está atacando durante la duración del juego. Estas escopetas de Airsoft son un tipo de arma por lo general largas (también existen los cañones cortos) y utilizadas como arma principal del juego por su jugador. Es un arma con mayor alcance de todas las que se pueden utilizar y cuenta con mayor atracción de manera visual para muchos de los jugadores.</p>
    <p>Este tipo de escopeta para Airsoft es de las preferidas por aquellos jugadores que tienen el papel de fusileros. Esto se debe a su gran parecido con las armas reales y por el gran tamaño que pueden tener. Puedes conseguir una gran cantidad de escopetas de bolas que son réplicas de las reales y puede ser usadas por jugadores expertos del Airsoft y también por los novatos en el área, así que no te preocupes y disfruta de tu escopeta de bolas.</p>'),
    (1, 'francotiradores', '/imagenes/subcategorias/francotiradores.jpg',
    '<p>Los francotiradores Airsoft juegan un papel muy importante dentro de este juego. Son jugadores que tienen que contar con mucha paciencia, inteligencia y a su vez una puntería increíble. Si tienes estas características principales, quiere decir que puedes ser excelente con los rifles para francotiradores en este juego que ha revolucionado al mundo entero desde su creación en Japón.</p>
    <p>La función principal de los francotiradores de Airsoft es ofrecerle protección a su equipo. Una tarea que se hace por medio de la observación de inteligencia y eliminando a los jugadores del equipo contrario con disparos realizados a larga distancia con diversas armas. Su objetivo es abatir a determinados jugadores del equipo contrario que pueden ser una amenaza para sus compañeros. Al realizar este tipo de disparos, los francotiradores logran que el equipo contrario quede con bajas y con movimientos limitados, por lo que consiguen una mejor penetración en terreno enemigo.'),
    (2, 'bolas de airsoft', '/imagenes/subcategorias/bolas-bbs.jpg', 'Los balines de Airsoft (conocidos como BBs) son proyectiles esféricos utilizados por las armas de airsoft. Por lo general, están hechos de plástico, suelen medir alrededor de 6 mm (0,24 pulgadas) de diámetro (aunque algunos modelos usan 8 mm) y pesan entre 0,20 y 0,40 g (3,1 a 6,2 g), siendo los pesos más comunes 0,20 gy 0,25 g. , mientras que las bolas de 0,28 g, 0,30 g, 0,32 g y 0,40 g también son habituales. Aunque los usuarios de airsoft los conocen con frecuencia como "BBs", estos BBs no son los mismos que los proyectiles metálicos de 4,5 mm que disparan las pistolas de BB ni los perdigones de 4,6 mm (0,180 pulgadas) de los que se originó el término "BB".'),
    (2, 'baterías y cargadores de batería', '/imagenes/subcategorias/baterias.jpg', 'En el airsoft, las baterías son utilizadas principalmente para alimentar las réplicas de armas eléctricas, conocidas como AEGs (Airsoft Electric Guns).'),
    (2, 'gas, co2 y mantenimiento', '/imagenes/subcategorias/gas-co2-y-mantenimiento.jpg', 'El gas y el CO2 son dos tipos de propelentes utilizados en las armas de airsoft para proporcionar la energía necesaria para disparar las BBs. Cada uno tiene características específicas y se utiliza en diferentes tipos de réplicas de armas. El lubricante ayuda a reducir la fricción entre las partes móviles del arma, como los engranajes en una AEG (Airsoft Electric Gun) o las partes móviles del mecanismo de blowback en armas de gas. Esto asegura un funcionamiento más suave y eficiente, lo que es crucial para la durabilidad del arma. Previene el Desgaste: Al disminuir la fricción, el lubricante también reduce el desgaste general de las partes móviles del arma. Esto es especialmente importante en componentes como pistones, cilindros y válvulas.'),
    (2, 'granadas y lanzagranadas', '/imagenes/subcategorias/granadas-y-lanzagranadas.jpg', 'Las granadas de airsoft están diseñadas para simular el efecto y la funcionalidad de las granadas reales. A menudo se utilizan para limpiar habitaciones o despejar trincheras y otros espacios cerrados, permitiendo a los jugadores golpear a múltiples objetivos simultáneamente.'),
    (2, 'cargadores pistola', '/imagenes/subcategorias/cargadores-pistola.jpg', 'un cargador es un dispositivo que almacena y alimenta las BBs (bolas de plástico que actúan como munición) a la réplica del arma. Es un componente crucial para el funcionamiento de las armas de airsoft, y su diseño y capacidad varían dependiendo del tipo de arma y del realismo deseado.'),
    (3, 'miras y red dot', '/imagenes/subcategorias/nombre.jpg', 'Las miras y red dot son son un accesorio airsoft complementario de cada uno de los jugadores. Permiten tener una pequeña ventaja sobre el adversario, ya que nos ayuda a visualizar y apuntar más rápido a grandes distancias'),
    (3, 'correas y landyards', '/imagenes/subcategorias/nombre.jpg', 'Las correas habilitan al jugador a utilizar sus manos en otras labores mientras su réplica primaria cuelga sin perderse.  El lanyard es una cuerda o cordón que se coloca alrededor del cuello y permite colgar complementos y accesorios airsoft.'),
    (3, 'monturas y raíles', '/imagenes/subcategorias/nombre.jpg', 'Las monturas y raíles para Airsoft son ampliamente utilizadas en este deporte ya que ayudan a colocar cualquier accesorio a nuestra replica. Es decir, nos permite colocar: teléfonos, cámaras, miras, linternas o cualquier objeto que nos ayudará a un mejor desempeño en el campo de batalla.'),
    (3, 'linternas y láseres', '/imagenes/subcategorias/nombre.jpg', 'Las linternas y láser Airsoft tienen muchos usos: desde planear emboscadas a dar señales, pasar mensaje en código, indicar posiciones, o simplemente para poder tener una visión nocturna. Son un accesorio de Airsoft necesario para todos los equipos, en especial en jornadas de juego nocturnas.'),
    (4, 'proteccion facial', '/imagenes/subcategorias/nombre.jpg', 'La protección en Airsoft por ningún motivo es sacrificable y en especial la PROTECCIÓN FACIAL, ya que es la encargada de cuidar gran parte de nuestro rosto y no queremos sufrir alguna lesión en cualquiera zona baja de nuestro rostro ya que es la carta de presentación de cualquier persona.'),
    (4, 'gafas', '/imagenes/subcategorias/nombre.jpg', 'La aplicación de gafas airsoft en el desarrollo de una incursión es altamente recomendable porque en un primer momento nos proporciona los elementos de protección necesarios ante cualquier disparo, es necesario destacar que no se recomienda bajo ningún concepto usar elementos similares de uso convencional o cotidiano, ya que la intensidad de una partida de este deporte puede ocasionar la pérdida o daño permanente del mismo.'),
    (4, 'chalecos', '/imagenes/subcategorias/nombre.jpg', 'el deportista debe poner en práctica el contar con todos los elementos de equipamiento necesarios como los chalecos, ya que los mismos en su mayoría proporcionan un nivel de cobertura altamente conocida con respecto a la exposición constante que se realiza ante los disparos, teniendo en cuenta que, los mismos en algunas ocasiones pueden impactar en el cuerpo con distintos tipos de lesiones.'),
    (4, 'portacargadores y pouch', '/imagenes/subcategorias/nombre.jpg', 'Como su nombre lo indica los PORTACARGADORES, POUCH son uno de los accesorios más buscados en la práctica de Airsoft, ya que nos permite llevar municiones de una forma fácil y segura, que no impide en nada el buen desarrollo en el campo de batalla.'),
    (4, 'cascos', '/imagenes/subcategorias/nombre.jpg', 'Dentro del equipamiento mínimo necesario para la práctica correcta de airsoft, se incluyen los cascos de uso obligatorio.  Son artículos de seguridad en el juego y son necesarios para asegurar un juego con una mínima exposición al daño craneal.  Además de que son físicamente atractivos como parte del uniforme.'),
    (5, 'Cámaras y gomas Hop-up', '/imagenes/subcategorias/nombre.jpg', 'La cámara de Hop Up es un componente crítico en cualquier réplica de airsoft, ya que es responsable de impartir un efecto de giro a la bola mientras sale del cañón. Esto permite que la bola se estabilice en el aire, mejorando la precisión y el alcance del disparo. En nuestra tienda online, ofrecemos una amplia gama de cámaras de Hop Up de diferentes fabricantes y modelos, incluyendo marcas líderes como Lonex, Prometheus, y Mad Bull.'),
    (5, 'Gearbox', '/imagenes/subcategorias/nombre.jpg', 'El gearbox es uno de los componentes más importantes de cualquier réplica de Airsoft. Es el corazón del arma, y es responsable de la mayoría de las funciones, desde la alimentación de BBs hasta el disparo. En esta categoría te ofrecemos una amplia selección de Gearbox (cajas de cambios) de Airsoft.'),
    (5, 'Engranajes', '/imagenes/subcategorias/nombre.jpg', 'Los engranajes son uno de los componentes más críticos en una réplica de airsoft, ya que son responsables de transferir la energía del motor al mecanismo de disparo. Por lo tanto, es importante que los engranajes estén diseñados y fabricados con precisión para asegurar un rendimiento óptimo y una larga vida útil.'),
    (5, 'Motores', '/imagenes/subcategorias/nombre.jpg', 'Ofrecemos una amplia selección de motores de alta calidad, diseñados específicamente para réplicas de airsoft. Nuestros motores son potentes y eficientes, lo que resulta en una mayor velocidad de disparo y una mejor respuesta del gatillo. Además, también ofrecemos motores de diferentes tamaños y potencias para que puedas personalizar tu sistema según tus necesidades. Todos nuestros motores son fabricados con materiales duraderos y de alta calidad para garantizar un rendimiento óptimo y una larga vida útil.'),
    (5, 'Pistón y cabeza pistón', '/imagenes/subcategorias/nombre.jpg', 'Los pistones y cabeza de pistones de alta calidad mejoran el rendimiento de tu réplica de airsoft. Encontrarás pistones y cabezas de pistones para airsoft de diferentes materiales y diseños, desde pistones reforzados con dientes metálicos para una mayor durabilidad, hasta cabezas de pistón de aluminio y juntas tóricas para mejorar la estanqueidad y reducir la pérdida de aire.');

INSERT INTO productos (identidad_fabricante, identidad_subcategoria, nombre, descripcion, detalles, precio, stock, novedad, tipo_descuento, descuento, fecha_creacion) VALUES
    (9, 2, 'Subfusil UCI 35', 'descripcion', 'detalles', 11.95, 0, false, 'porcentual', 10, '2015-06-01'),
    (5, 1, 'G&G RK47 IMITATION WOOD STOCK BLOWBACK', 'descripción', 'detalles', 210.40, 0, false, "sin_descuento", null, '2016-08-18'),
    (4, 7, 'CARGADOR BALANCEADOR LIPO 2-3 CELDAS 7.4V/11.1V DUEL CODE', 'descripcion', 'detalles', 18.95, 10, false, "sin_descuento", null, '2017-05-15'),
    (1, 1, 'AMOEBA AM-008 M4-CQBR 7" TAN', 'descripción', 'detalles', 189.95, 10, false, "sin_descuento", null, '2017-06-09'),
    (5, 2, 'AEG ARP 9 G&G', 'descripcion', 'detalles', 269.95, 10, false, 'absoluto', 10, '2017-10-09'),
    (10, 2, 'TOKYO MARUI MP7A1', 'descripción', 'detalles', 355.95, 10, false, "sin_descuento", null, '2018-08-13'),
    (10, 10, 'Cargador MP7 Marui GBB', 'descripcion', 'detalles', 54.95, 30, false, "sin_descuento", null, '2019-02-06'),
    (7, 1, 'Lancer Tactical LT-02C MK18 Pack', 'descripción', 'detalles', 144.90, 10, false, "sin_descuento", null, '2019-09-23'),
    (3, 1, 'DBOYS METÁLICA M4A1 (3681M) AEG', 'descripción', 'detalles', 195.00, 10, false, "sin_descuento", null, '2020-02-21'),
    (10, 3, 'Tokyo Marui FNX-45', 'descripcion', 'detalles', 189.99, 10, false, "sin_descuento", null, '2020-07-24'),
    (5, 20, 'Cámara Hop Up RK - G&G', 'descripcion', 'detalles', 37.95, 10, false, 'absoluto', 4, '2020-09-25'),
    (8, 6, 'Bolas Trazadoras 0.25g Professional Performance 2000bbs - NIMROD', 'descripcion', 'detalles', 15.90, 50, false, "sin_descuento", null, '2020-10-05'),
    (8, 6, 'Bolas Trazadoras 0.30g Professional Performance 2000bbs - NIMROD', 'descripcion', 'detalles', 24.90, 50, false, "sin_descuento", null, '2020-10-05'),
    (3, 13, 'Anilla Enganche QD DBOYS', 'descripcion', 'detalles', 5.45, 0, false, "sin_descuento", null, '2020-11-10'),
	(4, 7, 'BATERIA LI-PO 11.1V 800MAH 15C T-DEAN DUEL CODE', 'descripcion', 'detalles', 15.95, 50, false, "sin_descuento", null, '2020-12-22'),
    (8, 8, 'Gas Standard Performance Green (10KG) 500ml - NIMROD', 'descripcion', 'detalles', 9.90, 30, false, "sin_descuento", null, '2021-01-13'),
    (8, 8, 'Gas Professional Performance Red (12kg) 500ml - NIMROD', 'descripcion', 'detalles', 11.90, 30, false, "sin_descuento", null, '2021-01-13'),
    (1, 1, 'MUTANT AM-M-004 - ARES AMOEBA', 'descripción', 'detalles', 289.95, 10, false, "absoluto", 10, '2021-08-27'),
    (9, 5, 'Francotirador L96 Upgraded con Mira y Bipode Negro - SAIGO', 'descripcion', 'detalles', 197.95, 30, false, "sin_descuento", null, '2021-07-20'),
	(10, 12, 'Correa 2 Puntos Tokyo Marui ajuste rápido - Tan', 'descripcion', 'detalles', 46.00, 30, false, "sin_descuento", null, '2021-10-08'),
    (9, 3, 'PISTOLA SAIGO DEFENSE 1911 MUELLE', 'descripcion', 'detalles', 9.95, 0, false, "sin_descuento", null, '2021-10-13'),
    (5, 5, 'G&G G960 SV', 'descripcion', 'detalles', 199.95, 30, false, "sin_descuento", null, '2021-10-20'),
    (10, 7, 'BATERÍA SOPMOD TOKYO MARUI 8.4V NiMH1300mAh', 'descripcion', 'detalles', 59.95, 30, false, "absoluto", 6, '2021-11-15'),
    (8, 6, 'BBS TRAZADORAS VERDES 0.20 PROFESSIONAL PERFORMANCE', 'descripcion', 'detalles', 13.90, 50, false, "sin_descuento", null, '2022-03-14'),
    (9, 9, 'GRANADA KAMIKAZE XS NEGRA - SAIGO DEFENSE', 'descripcion', 'detalles', 89.95, 30, false, "sin_descuento", null, '2022-12-06'),
    (9, 10, 'CARGADOR MK1 CO2 23 RDS - SAIGO DEFENSE', 'descripcion', 'detalles', 4.95, 30, false, "sin_descuento", null, '2023-01-19'),
    (10, 4, 'ESCOPETA M870 BREACHER - MARUI', 'descripcion', 'detalles', 329.99, 30, false, "sin_descuento", null, '2023-02-15'),
    (9, 9, 'GRANADA KAMIKAZE XS GOLD - SAIGO DEFENSE', 'descripcion', 'detalles', 119.50, 30, false, "sin_descuento", null, '2023-05-02'),
    (2, 14, 'LINTERNA BO PL350', 'descripcion', 'detalles', 74.95, 30, false, "sin_descuento", null, '2023-10-24'),
    (2, 4, 'ESCOPETA FABARM STF/12-18" MUELLE DE 3 TIROS', 'descripcion', 'detalles', 99.00, 30, false, "sin_descuento", null, '2023-11-29'),
    (4, 11, 'MIRA PUNTO ROJO G2 TAN - DUEL CODE', 'descripcion', 'detalles', 58.90, 30, false, "sin_descuento", null, '2024-01-19');
    
/*Productos nuevos*/
INSERT INTO productos (identidad_fabricante, identidad_subcategoria, nombre, descripcion, detalles, precio, stock, novedad, fecha_creacion) VALUES
    (4, 8, 'MIRA PUNTO ROJO Y VERDE COMPAC NEGRA - DUEL CODE', 'descripcion', 'detalles', 48.95, 30, true, '2024-01-22'),
    (10, 1, 'TOKYO MARUI SAIGA-12K', 'descripción', 'detalles', 589.95, 10, true, '2024-02-13'),
    (5, 8, 'FRANCOTIRADOR DE CO2 M1903 A3 - G&G', 'descripcion', 'detalles', 584.90, 5, true, '2024-02-29');
    
/*Contraseñas encriptadas mediante bcrypt a través de https://bcrypt-generator.com/*/
INSERT INTO usuarios (username, password, enabled, nombre, apellido1, apellido2, role) VALUES
    ('ricardo@nombregrupo.com', '$2a$12$RLDKdQu8djPEv7/7/rmF1ePUAg0sPUMCPaXh0uhG.w1QH95SB20JC', true, 'Ricardo', 'Deza', 'Roanes', 'ROLE_ADMIN'),
    ('enrique@nombregrupo.com', '$2a$12$I4A.WrC/YPPN2d4Coy6LH.AbcEX4ANRGNWkW1KBtDIiNDDNTh6BS.', true, 'Enrique', 'Várgas', null, 'ROLE_ADMIN');

/*Recordar que al definir la tabla usuarios dijos que por defecto el role es ROLE_USER*/
INSERT INTO usuarios (username, password, enabled, nombre, apellido1, apellido2) VALUES
    ('j-druimor@example.com', '$2a$12$.Fqr3K6qDvQ3Xci3SOhvPO8IqM83RD11JwiloYeJjcekPgT.T3oz6', true, 'Juan Diego', 'Ruiz', 'Moreno'), /*Contraseña es j-druimor*/
    ('sercal___@example.com', '$2a$12$5WX81WmyfDI/gISrlT8q/uINDkPXV3jCNXRVJ0ygDy8UuSWQj.qoO', true, 'Sergio', 'Calderón', null), /*Contraseña es sercal___*/
    ('j-jmig___@example.com', '$2a$12$z9spIpwXEmVo8iIrKh3dSe9od7JhpMIGZ6TVikiArH/upOBMRT9Ji', true, 'Juan José', 'Míguez', null),
    ('davgai___@example.com', '$2a$12$WMP5oUHfxqx98V6s7f16xue4AOe0zljFN7B/UTGvuqD.rJSlGHfF2', true, 'David', 'Gail', null),
    ('rafdiaval@example.com', '$2a$12$vLklpTuA8V56pBvRwNEkFOENxMBb2rgpwIN5u8cXwqEw5jUwm65PG', true, 'Rafaela', 'Díaz', 'Valiente'),
    ('carlarldg@example.com', '$2a$12$/QleNIa5ileNPwHo9KxzOO1CqnI1HCWkYpo6bEPGhpcul4Gxf/MUC', true, 'Carlos', 'Larrañaga', 'Ladrón de Guevera'), /*Contraseña es carlarldg*/
    ('j-mmarper@example.com', '$2a$12$n0IVHU/KjKSkz0C2xfzGxua9UDVFrbjJmzMsUFZe4eShavuicKI5i', true, 'José Manuel', 'Martín', 'Pérez'),
    ('angd-amiq@example.com', '$2a$12$OeDV8u3Dc2EyV7XKG28XkOD26TVVWiiBDsQZ5k/.CJTf1qUBDwOxK', true, 'Ángel', 'de Andrés', 'Miquel'),
    ('j-lmanagu@example.com', '$2a$12$c9TQgQGv4jOy2HB84INvNuYWqGk9fcqGpUN6zzX6NEemqYNt0kM.m', true, 'José Luiz', 'Manzano', 'Agudo'), /*Contraseña es j-lmanagu*/
    ('cricersan@example.com', '$2a$12$fbN/q.pLHAJex2YlXqlNjOcEjczwDQmQK/mjXNgZdu9qS0FcpMN86', true, 'Cristina', 'Cerviá', 'Sancho'),
    ('a-pcalles@sqlmail.com', '$2a$12$/0CJEdO7n28wKoe144FYJ.fHQkZbh7/KCPpKajfHGdfA12wuU9MeG', true, 'Armando Pascual', 'Calvo', 'Lespier'),
    ('w-hpra___@sqlmail.com', '$2a$12$5BlMQj/Hq1Uyp649QdeEteXrzs67QrNv2F0OtKUW6VBp9jt4Q3GjS', true, 'William Henry', 'Pratt', null), /*Contraseña es w-hpra___*/
    ('edugomman@sqlmail.com', '$2a$12$hZkuTPBSn96VGx1KNnZJbuPBdavsTc7nTdzeeUJauyMTY8ZGQzE9W', true, 'Eduardo', 'Gómez', 'Manzano'),
    ('flochim-m@sqlmail.com', '$2a$12$HWLqtlKcdB.7C8DDHtUiEum3Yby0S/Z9Aw3AvyZ/cLzSOO6DaCLKC', false, 'Florinda', 'Chico', 'Martín-Mora');

INSERT INTO resenas (identidad_producto, identidad_usuario, valoracion, titulo, fecha_creacion, comentario) VALUES
    (4, 3, 5, 'MUY BUENA CALIDAD', '2019-11-27',
    'Muy contento con la compra, la recomiendo al 100%'),
    (8, 4, 5, 'Muy buena replica.', '2019-12-05',
    'hola, el arma va muy muy bien, muy buena calidad precio y el envió a ido muy rápido, gracias'),
    (8, 5, 4, 'Perfecta para empezar', '2020-02-16',
    'Es la primera réplica que adquiero, y la compre directamente en tienda, gracias a la recomendación de Javi. Estoy muy satisfecho.
    Puntos fuertes: Es robusta, los internos son bastante buenos para el precio. Viene ya con bateria, el grip y todo eso, no hace falta esa inversión plus. La puntería es buena y con los fps que tiene se asemeja a otras réplicas más caras. La culata tiene bastantes posiciones. En general, es una réplica sorprendentemente buena para empezar y sin hacer una inversión excesiva.
    Puntos débiles: Algunos cargadores (como los de ares) encajan bien pero con bastante holgura, lo que no afecta a la funcionalidad. La mira delantera sujeta los railes, por lo que quitarla es inviable, aun asi no molesta si se pone un red dot en el rail de puente. Viene sin enganche único en la parte de la culata, por lo que habría que adquirirlo si se lo quieres poner.
    Cabe decir que soy muy meticuloso, pero en general solo tengo buenas críticas para esta réplica aunque destaque esos puntos débiles. Por este precio, para iniciarse o si te interesa esta marca, la verdad es que es una pasada. La recomiendo sin duda.
    La atención por parte de Airsoft Yecla y su experiencia la verdad es que la hacen una tienda excelente donde comprar.'),
    (4, 6, 5, 'Excelente', '2020-04-14',
    'Exelente trato me la enviaron a Barcelona la réplica buen peso , se ve muy bien , y buena precisión por su precio exelente'),
    (9, 6, 5, 'Recomendable', '2020-05-07',
    'Va perfecta, y tira muy muy bien esta de lujo para su precio!! ,Donde vas a encontrar una réplica de metal por ese precio? En ningún sitio la recomiendo'),
    (9, 6, 5, 'M4', '2020-05-12',
    'Va muy bien ,yo tengo esa misma que pedí hace poquito la verda estoy muy contento la recomiendo.'),
    (2, 7, 5, 'Perfecta!!!!', '2020-05-29',
    'Buena calidad, terminaciones y potencia. Servicio de venta excelente, muy recomendable de 6 estrellas.'),
    (2, 7, 5, 'Perfecta!!!!', '2020-05-29',
    'Buena calidad, terminaciones y potencia. Servicio de venta excelente, muy recomendable de 6 estrellas.'),
    (1, 8, 5, 'BRUTAL', '2020-08-03',
    'Relación precio calidad BRUTAL. Que me corrija el moderador, pero creo que es la única réplica que no es escala 1:1 , creo que es unos centímetros más pequeña.
    Es la pistola de muelle más potente que he probado . Alcanza casi el doble que otras pistolas de muelle y casi tres veces más que los por ejemplo la D90 eléctrica. 
    Tiene piezas desmontables para hacerla más larga. El ruido que hace el muelle al disparar , ... pues se nota que es barata , pero más potente y precisa que 
    muchas otras. Muy recomendable , pero cuidado con dejársela a los niños . Si te dan con esta en la espalda o en una pierna con ropa , ¡ pica ¡ , . y rebotan las bolitas con fuerza'),
    (6, 9, 5, 'Perfecto', '2020-08-10',
    'Aún no he disparado con ella porque me acaba de llegar, pero he probado otras y es una maravilla sobre todo en entornos cqb. Además me ha sorprendido que el arma viene marcada con su número de armero y los papeles correspondientes. 100%recomendable la réplica y la tienda.'),
    (5, 10, 5, 'ESPECTACULAR!', '2020-08-26',
    'Va de lujo, super manejable, perfecta para campos pequeños y con gran alcance. Una gran compra!'),
    (1, 11, 5, 'Brutal', '2020-09-24',
    'Muy buena.'),
    (1, 14, 5, 'Recomendable 100%', '2020-10-30',
    'Buena calidad/precio. Envió rápido.'),
    (9, 13, 4, 'Bien pero mal', '2021-03-21',
    'el arma esta muy bien y la recomiendo mucho pero su mira por defecto esta muy mal alineada y para saber donde das tienes que corregir el disparo a ojo'),
    (10, 12, 5, 'Fnx 45', '2021-06-15',
    'Espectacular el tiro la recomiendo mucho para principiantes y para expertos'),
    (19, 14, 5, 'Francotirador', '2021-10-19',
    'Muy buena calidad'),
    (21, 15, 5, 'La mejor de las baratas', '2023-06-15',
    'Compré tres distintas para mis sobrinos y esta es la más precisa de las tres. Para el precio que tiene me parece muy útil.');

START TRANSACTION;

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (3, 'Juan Diego', 'Ruiz', 'direccion JR', 'España', 'Bormujos', '+346XXXXXXXX', 'NACEX', '2024-04-01');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 3, 1),
    (@identidad_pedido, 9, 2),
    (@identidad_pedido, 15, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (3, 'Juan Diego', 'Ruiz', 'direccion JR', 'España', 'Bormujos', '+346XXXXXXXX', 'NACEX', '2024-04-01');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 3, 1),
    (@identidad_pedido, 9, 2),
    (@identidad_pedido, 15, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (5, 'Juan José', 'Míguez', 'direccion JM', 'Portugal', 'Lisboa', '+346XXXXXXXX', 'Recogida_en_tienda', '2024-04-02');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 25, 1);
    
INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (5, 'Juan José', 'Míguez', 'direccion JM', 'Portugal', 'Lisboa', '+346XXXXXXXX', 'CTT_Express', '2024-04-03');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 26, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (3, 'Germán', 'Areta', 'direccion GA', 'España', 'Madrid', '+346XXXXXXXX', 'CTT_Express', '2024-04-04');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 5, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (10, 'Miguel', 'Aguirrezabala', 'direccion MA', 'España', 'Azpeitia', '+346XXXXXXXX', 'CTT_Express', '2024-04-05');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 6, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (7, 'Rafaela', 'Díaz', 'direccion RD', 'España', 'Marbella', '+346XXXXXXXX', 'CTT_Express', '2024-04-07');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 6, 1),
    (@identidad_pedido, 19, 3);
    
INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (8, 'Ramiro', 'Pacheco', 'direccion RP', 'España', 'Madrid', '+346XXXXXXXX', 'CTT_Express', '2024-04-07');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 15, 2),
    (@identidad_pedido, 16, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (9, 'José Manuel', 'Martín', 'direccion JM', 'España', 'Casavieja', '+346XXXXXXXX', 'CTT_Express', '2024-04-09');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 9, 3);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (3, 'Juan Diego', 'Ruiz', 'direccion JR', 'España', 'Bormujos', '+346XXXXXXXX', 'CTT_Express', '2024-04-15');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 15, 2);

INSERT INTO pedidos (identidad_usuario, nombre, apellidos, direccion, pais, ciudad, numero_telefono_movil, metodo_envio, fecha_pedido) VALUES
    (7, 'Nuria', 'Berenguer', 'direccion NB', 'España', 'Gerona', '+346XXXXXXXX', 'NACEX', '2024-04-16');
SET @identidad_pedido = LAST_INSERT_ID();
INSERT INTO linea_facturacion (identidad_pedido, identidad_producto, cantidad) VALUES 
	(@identidad_pedido, 12, 1);

COMMIT;
	
/*Recordar identidad_pedido es único*/
INSERT INTO envios (identidad_pedido, fecha_envio) VALUES
    (1, '2024-04-04'),
    (3, '2024-04-05'),
    (4, '2024-04-06'),
    (5, '2024-04-06'),
    (6, '2024-04-08'),
    (7, '2024-04-10'),
    (8, '2024-04-13'),
    (9, '2024-04-13');

INSERT INTO imagenes(identidad_producto, imagen_url) VALUES
    (1, 'uci-35_1.jpg'),(1, 'uci-35_2.jpg'),(1, 'uci-35_3.jpg'),(1, 'uci-35_4.jpg'),
    (2, 'gg-rk47-imitation-wood-stock-blowback_1.jpg'),(2, 'gg-rk47-imitation-wood-stock-blowback_2.jpg'),(2, 'gg-rk47-imitation-wood-stock-blowback_3.jpg'),
    (3, 'cargador-lipo-duelcode_1.jpg'), (3, 'cargador-lipo-duelcode_2.jpg'),
    (4, 'amoeba-am-008-m4-cqbr-7-tan_1.jpg'),(4, 'amoeba-am-008-m4-cqbr-7-tan_2.jpg'),
    (5, 'aeg-arp-9-gg_1.jpg'),(5, 'aeg-arp-9-gg_2.jpg'),(5, 'aeg-arp-9-gg_3.jpg'),(5, 'aeg-arp-9-gg_4.jpg'),(5, 'aeg-arp-9-gg_5.jpg'),
    (6, 'tokyo-marui-mp7a1_1.jpg'),(6, 'tokyo-marui-mp7a1_2.jpg'),(6, 'tokyo-marui-mp7a1_3.jpg'), (6, 'tokyo-marui-mp7a1_4.jpg'),(6, 'tokyo-marui-mp7a1_5.jpg'),(6, 'tokyo-marui-mp7a1_6.jpg'),(6, 'tokyo-marui-mp7a1_7.jpg'),(6, 'tokyo-marui-mp7a1_8.jpg'),
    (7, 'cargador-mp7-marui_1.jpg'), (7, 'cargador-mp7-marui_2.jpg'),
    (8, 'lancer-tactical-lt-02c-mk18-pack_1.jpg'),(8, 'lancer-tactical-lt-02c-mk18-pack_2.jpg'),(8, 'lancer-tactical-lt-02c-mk18-pack_3.jpg'),
    (9, 'dboys-metalica-m4a1-3681m-aeg_1.jpg'),(9, 'dboys-metalica-m4a1-3681m-aeg_2.jpg'),(9, 'dboys-metalica-m4a1-3681m-aeg_3.jpg'),(9, 'dboys-metalica-m4a1-3681m-aeg_4.jpg'),
    (10, 'tokyo-marui-mp7a1_1.jpg'),(10, 'tokyo-marui-mp7a1_2.jpg'),(10, 'tokyo-marui-mp7a1_3.jpg'),(10, 'tokyo-marui-mp7a1_4.jpg'),
	(11, 'camara-hop-up-rk-gg_1.jpg'),
    (12, 'bolas-trazadoras-025g-professional-performance-2000bbs_1.jpg'),
    (13, 'bolas-trazadoras-030g-professional-performance-2000bbs_1.jpg'),
    (14, 'anneau-de-crochet-qd-dboys_1.jpg'),(14, 'anneau-de-crochet-qd-dboys_2.jpg'),
    (15, 'bateria-li-po-111v-800mah-15c-t-dean-duel-code_1.jpg'),
    (16, 'gas-standard-performance-green-500ml_1.jpg'),
    (17, 'gas-professional-performance-red-500ml_1.jpg'),
    (18, 'mutant-am-m-004-ares-amoeba_1.jpg'),(18, 'mutant-am-m-004-ares-amoeba_2.jpg'),(18, 'mutant-am-m-004-ares-amoeba_3.jpg'),(18, 'mutant-am-m-004-ares-amoeba_4.jpg'),
    (19, 'francotirador-l96-upgraded-con-mira-y-bipode-negro-saigo_1.jpg'),(19, 'francotirador-l96-upgraded-con-mira-y-bipode-negro-saigo_2.jpg'),(19, 'francotirador-l96-upgraded-con-mira-y-bipode-negro-saigo_3.jpg'),
    (21, 'pistola-saigo-defense-1911-muelle_1.jpg'),
    (22, 'gg-g960-sv_1.jpg'),(22, 'gg-g960-sv_2.jpg'),(22, 'gg-g960-sv_3.jpg'),(22, 'gg-g960-sv_4.jpg'),(22, 'gg-g960-sv_5.jpg'),
    (27, 'escopeta-m870-breacher-marui_1.jpg'),
    (30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_1.jpg'),(30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_2.jpg'),(30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_3.jpg'),
    (30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_4.jpg'),(30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_5.jpg'),(30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_6.jpg'),
    (30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_7.jpg'),(30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_8.jpg'),(30, 'escopeta-fabarm-stf12-18-muelle-de-3-tiros_9.jpg'),
	(31, 'mira-punto-rojo-g2-tan-duel-code_1.jpg'),(31, 'mira-punto-rojo-g2-tan-duel-code_2.jpg'),(31, 'mira-punto-rojo-g2-tan-duel-code_3.jpg'),
    (31, 'mira-punto-rojo-g2-tan-duel-code_4.jpg'), (31, 'mira-punto-rojo-g2-tan-duel-code_5.jpg'),
    (32, 'linterna-bo-pl350_1.jpg'),(32, 'linterna-bo-pl350_2.jpg'),(32, 'linterna-bo-pl350_3.jpg'),(32, 'linterna-bo-pl350_4.jpg'),
    (32, 'linterna-bo-pl350_5.jpg'),(32, 'linterna-bo-pl350_6.jpg'),(32, 'linterna-bo-pl350_7.jpg'),
    (33, 'tokyo-marui-saiga-12k_1.jpg'),(33, 'tokyo-marui-saiga-12k_2.jpg'),(33, 'tokyo-marui-saiga-12k_3.jpg'),(33, 'tokyo-marui-saiga-12k_4.jpg'),
    (33, 'tokyo-marui-saiga-12k_5.jpg'),
    (34, 'francotirador-de-co2-m1903-a3-gg_1.jpg'),(34, 'francotirador-de-co2-m1903-a3-gg_2.jpg'),(34, 'francotirador-de-co2-m1903-a3-gg_3.jpg'),
    (34, 'francotirador-de-co2-m1903-a3-gg_4.jpg');
    
/*UPDATE pedidos SET estado = 'cancelado' WHERE id_pedido = 2;
UPDATE envios SET fecha_entrega = '2024-04-11', numero_documento_identidad_receptor = 'nif receptor' WHERE id_envio = 1;
UPDATE envios SET fecha_entrega = '2024-04-15', numero_documento_identidad_receptor = 'nif receptor' WHERE id_envio = 2;
UPDATE envios SET fecha_entrega = '2024-04-15', numero_documento_identidad_receptor = 'nif receptor' WHERE id_envio = 3;
UPDATE envios SET fecha_entrega = '2024-04-15', numero_documento_identidad_receptor = 'nif receptor' WHERE id_envio= 4;
UPDATE envios SET fecha_entrega = '2024-04-17', numero_documento_identidad_receptor = 'nif receptor' WHERE id_envio = 5;
UPDATE envios SET fecha_entrega_vuelta_almacen = '2024-04-20', comentario = 'Tres intentos fallidos de entrega. Imposible contactar por teléfono. Devolvemos a almacén.' WHERE id_envio = 6;
*/
SELECT * FROM fabricantes;
SELECT * FROM usuarios;
SELECT * FROM productos;
SELECT * FROM resenas;
SELECT * FROM pedidos;
SELECT * FROM linea_facturacion;
SELECT * FROM envios;
/*SELECT * FROM usuarios WHERE active = TRUE;*/
