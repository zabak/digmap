package pt.utl.ist.lucene.treceval.geotime.utiltasks;

import nmaf.util.DomUtil;
import org.w3c.dom.Document;
import pt.utl.ist.lucene.analyzer.LgteDiacriticFilter;
import pt.utl.ist.lucene.treceval.geotime.webservices.CallWebServices;
import pt.utl.ist.lucene.web.assessements.services.Server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Jorge
 * Date: 7/Jul/2011
 * Time: 20:41:15
 * To change this template use File | Settings | File Templates.
 */
public class CallPlaceMaker extends HttpServlet{

    public static void main(String[] args) throws Exception {
        String text = "Felizmente que vem j� a� o �Campeonato Nacional�, � tudo quanto se nos oferece dizer, a prop�sito do Benfica-Vianense, disputado no �ltimo domingo. Continuamos a insistir que este coment�rio � ditado pela posi��o tradicionalmente independente que o jornal tem em rela��o ao clube, que se empenha em servir. Repetimos: desconhecemos a posi��o oficial do Benfica, no assunto, mas no nosso entender, estes jogos t�m sido uma �chumbada� das antigas. Para todos: directores (principalmente os do pelouro financeiro...), t�cnicos (que n�o podem tirar qualquer conclus�o em jogos t�o f�ceis), jogadores (principalmente os que gostam realmente de futebol), p�blico (a bocejar...) e cr�tica... que tem de fazer a cronicazinha do costume, de jogos que, al�m dos golos, nada t�m para contar. Quem duvida, pois, que este insonso princ�pio de �poca, tem de levar uma volta? H� quem defenda a tese que estes jogos inaugurais, com equipas mais fracas, s�o bons para estruturar, para arrumar a equipa, para dar rodagem aos jogadores. Puro engano: antes nos parece que estes desafios t�m servido para amolecer os jogadores, desabituando-os de lutar, da necessidade de for�ar a vit�ria. H� um amolecimento progressivo na equipa, em fun��o das mil e uma facilidades que v�o encontrando. Com resultados de 6, 8 e 9 bolas marcadas, o que � [que] pode convidar a um maior empenho, a um futebol mais bonito e r�pido? Amigos, felizmente no domingo que vem j� h� Campeonato. E n�o nos admir�vamos nada que a m�quina emperrasse, em fun��o destes quatro domingos a meio-vapor, a que a �Ta�a� nos obrigou. Mas pergunta-se: ent�o, em presen�a de advers�rios de t�o fraca envergadura, n�o teria sido o momento exacto para partir para a exibi��o grande, que tem vindo a faltar desde o princ�pio da �poca? As exibi��es em grande s� aparecem por acaso ou... surgem a partir de alguma coisa, do obst�culo que � preciso remover, da oposi��o f�rrea, da vontade de vencer... Entrar em campo, a ganhar por 8-1, contra um advers�rio de quilate menor e arrancar a tal grande exibi��o � dif�cil, muito dif�cil. Falta o est�mulo. S� por acaso... E ent�o, como foi no domingo passado? Jogou-se e marcou-se. N�o houve a alegria do transcendente, houve a satisfa��o do dever cumprido: 9-0 dispensa coment�rios. Houve bocadinhos de �deixa a andar� e houve bocadinhos de �vamos l� mostrar as nossas habilidades�. Houve um primeiro tempo em que o Vianense deu tudo por tudo e o Benfica n�o se preocupou, que �aquilo n�o ia a matar...� e uma segunda parte em que, perdoe-se a fantasia, havia a impress�o em que se Desid�rio e alguns dos seus pares n�o iam morrer, ali, dentro do campo, n�o acabavam, no entanto, a viagem at� Terras de Santa Luzia. Ao fim e ao cabo, mesmo n�o prevista, n�o deixa de ser uma t�ctica, deixando os jogadores do Vianense darem r�plica na primeira parte (�semear�), tem-se mais que certo o seu esgotamento na segunda (depois, �colher�). E que houve mais, no �ltimo Benfica-Vianense? Ah, muito calor... Calor que tamb�m d� motivo a um pequeno �suelto�, para al�m do que tenha estado na origem de um jogo logicamente �morno�. Aproveitamos para falar da const�ncia do interesse do bom p�blico benfiquista. Francamente, com 8-1 trazidos de Viana do Castelo, com o �calor�o� imenso que fazia, n�o �amos a contar com muita gente no nosso Est�dio. E estava pouca, realmente, mas no sector reservado aos s�cios, ainda havia larga multid�o, que marcou a sua presen�a, que desprezou o inc�modo do sol quente e da garganta seca, para irem ver o �seu� Benfica. A nota mais alta do encontro, para os entusiastas da bancada! A equipa do Benfica! Rolou, fez mais uma sess�o de �futebol de f�rias�. Chamada especial para a defesa: a coes�o continua ausente e � aqui estar� a nota de alarme � isso foi poss�vel de verificar, mesmo contra o Vianense... N�o fora a ingenuidade dos atacantes minhotos e a nossa defesa, a esta hora, podia estar �envergonhada�. Modifica��es no xadrez: Cav�m voltou a defesa-direito, mas nunca teve a quem marcar. Calado apareceu como �n.� 5� e andou ao sabor da corrente, j� que o desafio n�o se prestava para exames. Ali�s este caso do meio-campo, da camisola �n� 5�, � assunto a pedir perca de algum tempo e que sobre ele nos debrucemos atentamente. Mas a hora � de colabora��o, de ajudarmos todos a equipa a encontrar-se e discord�ncias p�blicas nada ajudam. Antes pelo contr�rio. Passemos, pois, adiante e frize-se que esta experi�ncia de domingo passado pouco interesse e oportunidade teve. Santana regressou em face da les�o de Eus�bio. Seria injusto opinar fosse o que fosse em face do jogo de domingo passado. J� emitimos a nossa opini�o, mais de uma vez: Santana, pela sua classe de jogador fino, interessa sempre � equipa. Mas... tem de durar os 90 minutos. Isso � que n�o pode ser esquecido. Aguardemos, pois, nova oportunidade para ver Santana em ac��o e avaliar da sua resist�ncia, j� que a categoria nunca pode estar em d�vida. A troca de posi��es entre Jos� Augusto e Yauca (em rela��o ao encontro de Viana do Castelo) pouco tem, tamb�m, a dizer, porque, no terreno, as permutas entre os dois foram constantes, sem se definir um verdadeiro lugar. Refer�ncia justa: ao Vianense, pelo seu aprumo, correc��o e lutar at� ao fim. Menos �rudes� que aquando do jogo no seu campo, foram extremamente disciplinados e simp�ticos. Aproveite-se a �pedra branca� para referir que a arbitragem foi boa. E que houve mais? Os golos, claro. Na primeira parte, foram s� dois. Aos 19 minutos: JOS� AUGUSTO veio embalado desde meio-campo �passou� pelos defesas e bateu, facilmente, Desid�rio, 1-0. Aos 37 minutos: SERAFIM rematou de longe, com for�a, o guardi�o vianense meteu as m�os � bola, mas ela escaldava, e deixou-a ir para dentro da baliza, 2-0. No segundo tempo, foram mais sete: aos 7 minutos: na sequ�ncia de um centro de Sim�es, YAUCA passou a marca para 3-0. Aos 22 minutos: falhan�o de Desid�rio, numa intercep��o, e JOS� AUGUSTO colocou, sem pressas, a bola dentro da baliza minhota, 4-0. Aos 25 minutos: o defesa forasteiro CERDEIRA, quando pretendia passar a bola ao seu guarda-redes, sentiu-se acossado por dois atacantes benfiquistas, tocou a bola com for�a demais, Desid�rio vinha na viagem e... 5-0. Aos 28 minutos: novo centro de Sim�es e SANTANA, com estrondo e efeito, a rematar e a fazer 6-0. Aos 29 minutos: gentileza de Jos� Augusto: com a defesa batida e a baliza aberta, preferiu �oferecer� o esf�rico a YAUCA, que n�o se fez rogado, marcou primeiro e agradeceu depois, 7-0. Aos 37 minutos: Sim�es n�o marcou... mas muitas vezes centrou para golo, como aconteceu desta feita, em que JOS� AUGUSTO, de cabe�a, estabeleceu o 8-0. Aos 42 minutos: finalmente, YAUCA encerrou a contagem que ficou em Benfica, 9-Vianense, 0. Sob a direc��o do escalabitano Fernando Velez, as duas equipas apresentaram: BENFICA � Costa Pereira: Cav�m, Raul e Cruz; Calado e Humberto; Jos� Augusto, Santana, Yauca, Serafim e Sim�es. VIANENSE � Desid�rio; Ramos, Cerdeira e Valdemar; Gerardo e Soares; Palhares, Pepe, Amaral, Manuelzinho e Carneiro. E foi tudo, neste Benfica-Vianense que nos ofereceu tempo quente, exibi��o morna, valorizada por golos de todos os tons num desafio de um tom s�. Felizmente que vem j� a� o �Nacional�...\n" +
                "Felizmente que vem j� a� o �Campeonato Nacional�, � tudo quanto se nos oferece dizer, a prop�sito do Benfica-Vianense, disputado no �ltimo domingo. Continuamos a insistir que este coment�rio � ditado pela posi��o tradicionalmente independente que o jornal tem em rela��o ao clube, que se empenha em servir. Repetimos: desconhecemos a posi��o oficial do Benfica, no assunto, mas no nosso entender, estes jogos t�m sido uma �chumbada� das antigas. Para todos: directores (principalmente os do pelouro financeiro...), t�cnicos (que n�o podem tirar qualquer conclus�o em jogos t�o f�ceis), jogadores (principalmente os que gostam realmente de futebol), p�blico (a bocejar...) e cr�tica... que tem de fazer a cronicazinha do costume, de jogos que, al�m dos golos, nada t�m para contar. Quem duvida, pois, que este insonso princ�pio de �poca, tem de levar uma volta? H� quem defenda a tese que estes jogos inaugurais, com equipas mais fracas, s�o bons para estruturar, para arrumar a equipa, para dar rodagem aos jogadores. Puro engano: antes nos parece que estes desafios t�m servido para amolecer os jogadores, desabituando-os de lutar, da necessidade de for�ar a vit�ria. H� um amolecimento progressivo na equipa, em fun��o das mil e uma facilidades que v�o encontrando. Com resultados de 6, 8 e 9 bolas marcadas, o que � [que] pode convidar a um maior empenho, a um futebol mais bonito e r�pido? Amigos, felizmente no domingo que vem j� h� Campeonato. E n�o nos admir�vamos nada que a m�quina emperrasse, em fun��o destes quatro domingos a meio-vapor, a que a �Ta�a� nos obrigou. Mas pergunta-se: ent�o, em presen�a de advers�rios de t�o fraca envergadura, n�o teria sido o momento exacto para partir para a exibi��o grande, que tem vindo a faltar desde o princ�pio da �poca? As exibi��es em grande s� aparecem por acaso ou... surgem a partir de alguma coisa, do obst�culo que � preciso remover, da oposi��o f�rrea, da vontade de vencer... Entrar em campo, a ganhar por 8-1, contra um advers�rio de quilate menor e arrancar a tal grande exibi��o � dif�cil, muito dif�cil. Falta o est�mulo. S� por acaso... E ent�o, como foi no domingo passado? Jogou-se e marcou-se. N�o houve a alegria do transcendente, houve a satisfa��o do dever cumprido: 9-0 dispensa coment�rios. Houve bocadinhos de �deixa a andar� e houve bocadinhos de �vamos l� mostrar as nossas habilidades�. Houve um primeiro tempo em que o Vianense deu tudo por tudo e o Benfica n�o se preocupou, que �aquilo n�o ia a matar...� e uma segunda parte em que, perdoe-se a fantasia, havia a impress�o em que se Desid�rio e alguns dos seus pares n�o iam morrer, ali, dentro do campo, n�o acabavam, no entanto, a viagem at� Terras de Santa Luzia. Ao fim e ao cabo, mesmo n�o prevista, n�o deixa de ser uma t�ctica, deixando os jogadores do Vianense darem r�plica na primeira parte (�semear�), tem-se mais que certo o seu esgotamento na segunda (depois, �colher�). E que houve mais, no �ltimo Benfica-Vianense? Ah, muito calor... Calor que tamb�m d� motivo a um pequeno �suelto�, para al�m do que tenha estado na origem de um jogo logicamente �morno�. Aproveitamos para falar da const�ncia do interesse do bom p�blico benfiquista. Francamente, com 8-1 trazidos de Viana do Castelo, com o �calor�o� imenso que fazia, n�o �amos a contar com muita gente no nosso Est�dio. E estava pouca, realmente, mas no sector reservado aos s�cios, ainda havia larga multid�o, que marcou a sua presen�a, que desprezou o inc�modo do sol quente e da garganta seca, para irem ver o �seu� Benfica. A nota mais alta do encontro, para os entusiastas da bancada! A equipa do Benfica! Rolou, fez mais uma sess�o de �futebol de f�rias�. Chamada especial para a defesa: a coes�o continua ausente e � aqui estar� a nota de alarme � isso foi poss�vel de verificar, mesmo contra o Vianense... N�o fora a ingenuidade dos atacantes minhotos e a nossa defesa, a esta hora, podia estar �envergonhada�. Modifica��es no xadrez: Cav�m voltou a defesa-direito, mas nunca teve a quem marcar. Calado apareceu como �n.� 5� e andou ao sabor da corrente, j� que o desafio n�o se prestava para exames. Ali�s este caso do meio-campo, da camisola �n� 5�, � assunto a pedir perca de algum tempo e que sobre ele nos debrucemos atentamente. Mas a hora � de colabora��o, de ajudarmos todos a equipa a encontrar-se e discord�ncias p�blicas nada ajudam. Antes pelo contr�rio. Passemos, pois, adiante e frize-se que esta experi�ncia de domingo passado pouco interesse e oportunidade teve. Santana regressou em face da les�o de Eus�bio. Seria injusto opinar fosse o que fosse em face do jogo de domingo passado. J� emitimos a nossa opini�o, mais de uma vez: Santana, pela sua classe de jogador fino, interessa sempre � equipa. Mas... tem de durar os 90 minutos. Isso � que n�o pode ser esquecido. Aguardemos, pois, nova oportunidade para ver Santana em ac��o e avaliar da sua resist�ncia, j� que a categoria nunca pode estar em d�vida. A troca de posi��es entre Jos� Augusto e Yauca (em rela��o ao encontro de Viana do Castelo) pouco tem, tamb�m, a dizer, porque, no terreno, as permutas entre os dois foram constantes, sem se definir um verdadeiro lugar. Refer�ncia justa: ao Vianense, pelo seu aprumo, correc��o e lutar at� ao fim. Menos �rudes� que aquando do jogo no seu campo, foram extremamente disciplinados e simp�ticos. Aproveite-se a �pedra branca� para referir que a arbitragem foi boa. E que houve mais? Os golos, claro. Na primeira parte, foram s� dois. Aos 19 minutos: JOS� AUGUSTO veio embalado desde meio-campo �passou� pelos defesas e bateu, facilmente, Desid�rio, 1-0. Aos 37 minutos: SERAFIM rematou de longe, com for�a, o guardi�o vianense meteu as m�os � bola, mas ela escaldava, e deixou-a ir para dentro da baliza, 2-0. No segundo tempo, foram mais sete: aos 7 minutos: na sequ�ncia de um centro de Sim�es, YAUCA passou a marca para 3-0. Aos 22 minutos: falhan�o de Desid�rio, numa intercep��o, e JOS� AUGUSTO colocou, sem pressas, a bola dentro da baliza minhota, 4-0. Aos 25 minutos: o defesa forasteiro CERDEIRA, quando pretendia passar a bola ao seu guarda-redes, sentiu-se acossado por dois atacantes benfiquistas, tocou a bola com for�a demais, Desid�rio vinha na viagem e... 5-0. Aos 28 minutos: novo centro de Sim�es e SANTANA, com estrondo e efeito, a rematar e a fazer 6-0. Aos 29 minutos: gentileza de Jos� Augusto: com a defesa batida e a baliza aberta, preferiu �oferecer� o esf�rico a YAUCA, que n�o se fez rogado, marcou primeiro e agradeceu depois, 7-0. Aos 37 minutos: Sim�es n�o marcou... mas muitas vezes centrou para golo, como aconteceu desta feita, em que JOS� AUGUSTO, de cabe�a, estabeleceu o 8-0. Aos 42 minutos: finalmente, YAUCA encerrou a contagem que ficou em Benfica, 9-Vianense, 0. Sob a direc��o do escalabitano Fernando Velez, as duas equipas apresentaram: BENFICA � Costa Pereira: Cav�m, Raul e Cruz; Calado e Humberto; Jos� Augusto, Santana, Yauca, Serafim e Sim�es. VIANENSE � Desid�rio; Ramos, Cerdeira e Valdemar; Gerardo e Soares; Palhares, Pepe, Amaral, Manuelzinho e Carneiro. E foi tudo, neste Benfica-Vianense que nos ofereceu tempo quente, exibi��o morna, valorizada por golos de todos os tons num desafio de um tom s�. Felizmente que vem j� a� o �Nacional�...\n" +
                "Como � do conhecimento geral, o nosso jogador Eus�bio da Silva Ferreira foi distinguido com a honraria de uma convoca��o para a equipa da FIFA, que, na pr�xima ter�a-feira, em Wembley, em jogo comemorativo do centen�rio do futebol no Reino Unido, defronta a selec��o inglesa. Igual distin��o foi concedida a M�rio Esteves Coluna, tendo sido estes os dois �nicos jogadores portugueses chamados � selec��o �maior� do futebol mundial. Infelizmente, a les�o sofrida por Coluna, tirou-lhe, desde logo, quaisquer possibilidades de estar presente no grande encontro do dia 23. E Eus�bio? Como sabem, em Viana do Castelo, as coisas correram mal para o brioso mo�ambicano, que se lesionou e viu a sua inclus�o na equipa da FIFA muito comprometida. A recupera��o tem corrido muito melhor e muito mais r�pido do que se previa, e desde a passada ter�a-feira que o valoroso internacional retomou a sua prepara��o. No entanto, no momento em que somos for�ados a mandar esta not�cia para a tipografia, ainda n�o � poss�vel precisar se Eus�bio se poder� deslocar ou n�o a Wembley. Para j�, registe-se a honraria dos convites, que muito dignificam, sem d�vida, o futebol benfiquista, o futebol portugu�s.\n" +
                "Amanh�, por volta das 10, 30 horas portuguesas (portanto 11, 30 locais), em Tarragona, no Hotel Imperial Tarraco, ser� feita a primeira jogada da segunda eliminat�ria da �Ta�a dos Clubes Campe�es Europeus�, a �nossa� prova. Vejamos, antes do mais, quem est� em prova e a quem eliminaram: MILAN �� o campe�o e s� agora entra na prova. BENFICA�Finalista vencido da �ltima edi��o da prova, desfez-se com facilidade do Distillery, campe�o da Irlanda do Norte. SPARTAK PLOVDIV � O representante b�lgaro teve tarefa relativamente f�cil no confronto com o campe�o da Alb�nia. DUKLA � J� conhecido do Benfica e equipa que, tradicionalmente, chega longe. Tamb�m teve um princ�pio f�cil, pois teve de se haver com o ing�nuo e bastante fraco campe�o da ilha de Malta, o Valleta. PART1ZAN � Outra equipa de tradi��es europeias, os jugoslavos tamb�m tiveram tarefa f�cil, pois tiveram de eliminar os cipriotas do Anorthosis.GORNIK � Os polacos, em certo ponto, foram uma surpresa, pois o favoritismo pertencia, em boa escala, aos seus advers�rios, o �ustria, de Viena, nosso duplo conhecido, porque j� veio perder por 5-1 ao nosso Est�dio e porque � l� que alinha, actualmente, Jos� �guas, um grande nome do historial futebol�stico do Benfica, uma saudade do futebol portugu�s. GALATASARAY � A grande surpresa desta primeira eliminat�ria: realmente, o futebol h�ngaro � tido como de muito maior potencial que o turco e muito principalmente o Ferencvaros era considerado como uma das equipas que, se n�o alinharia entre os grandes favoritos, estaria, pelo menos, numa segunda linha de nomes a considerar para a discuss�o final. Mas os turcos come�aram por vencer, em Istambul, sensacionalmente, por 4-0 e, aquando da desloca��o a Budapeste, no passado s�bado, jogaram com as necess�rias cautelas defensivas, bastantes para perderem apenas par 2-0. E os turcos continuam em prova... D�NAMO � Num confronto equilibrado os romenos seguem na prova, em preju�zo dos alem�es orientais do Motor Jena. M�NACO �Para o clube de Rainier tudo foi f�cil tamb�m pois os gregos do AEK n�o constitu�ram perigo de maior. JEUNESSE ESCH � Os campe�es do Luxemburgo foram verdadeiramente espectaculares, pois foram capazes de recuperar uma desvantagem de tr�s golos. Na verdade, tendo perdido, na Finl�ndia, frente ao Valkaekosken, por 4-1, os homens do Principado, com calma e categoria, conseguiram obter um saboroso triunfo por 4-0. Sensacional, na verdade. NORRKOPING �Outro velho conhecido nosso, ainda de recente data, pois o campe�o sueco foi o nosso primeiro advers�rio, na �poca passada, na �Ta�a dos Clubes Campe�es Europeus�. Tamb�m foi uma classifica��o que deu que falar, pois tiveram de defrontar um standard, de boas tradi��es na prova. Perdendo por 1-0 na B�lgica, os suecos souberam ganhar por 2-0, no seu reduto. Foi pouco... mas foi o bastante. REAL MADRID - Um dos �grandes-senhores� da prova, que foi submetido logo a rude confronto, na primeira eliminat�ria. Saiu-lhe o campe�o da Esc�cia, o Glasgow Rangers, tido como um dos favoritos. Em Glasgow, ainda as coisas foram um pouco dif�ceis e o Real Madrid ganhou � com algumas retic�ncias... por parte dos locais, mas com inteira justi�a, segundo a cr�tica neutra � por 1-0. Depois, em Madrid, foi uma parada de facilidades... e 6-0, estrondosa vit�ria dos �merengues�! H� que contar com eles, os diab�licos �brancos� de Madrid. INTERNAZIONALE � Outro dos grandes favoritos e que teve de lutar muito para continuar em prova, merc� do sacrif�cio do campe�o ingl�s Everton, que tinha leg�timas aspira��es a ir mais longe. Mas eram dois �galos� para o mesmo poleiro e os pupilos de Herrera, com sorte e saber puderam mandar os ingleses embora. BOR�SSIA DORTMUND - Outro favorito da segunda linha, prop�cio a melhorar com a continua��o na prova. Eliminaram o noruegu�s Skl Lyyn. PSV� Os campe�es da Holanda disputaram uma eliminat�ria equilibrada com os dinamarqueses do Esbjerg e a sua classifica��o tem foros de muita l�gica. ZURICH � Finalmente, temos os su��os. Repetiram o que fizeram os portugueses: eliminaram irlandeses, apenas com a diferen�a, em rela��o ao Benfica, dos seus irlandeses, o Dundalk, serem sulistas e republicanos, pois representavam a Rep�blica da Irlanda. E pronto: aqui tem o leitor, em tra�os muito breves, a hist�ria dos dezasseis sobreviventes da primeira eliminat�ria da �Ta�a dos Clubes Campe�es Europeus� e que amanh�, em Tarragona, ter�o os seus nomes sorteados para o acasalamento da segunda eliminat�ria. Que calhar� ao Benfica? Um j� conhecido, como o Dukla ou o Norrkoping? Uma das �feras� como a Real, o Inter ou o tamb�m conhecid�ssimo Milan? Ou um dos considerados mais acess�veis, como os campe�es da Holanda ou do Luxemburgo? Podem ir fazendo os vossos palpites, mas, o mais certo, � ainda aguardar por amanh�. Em Tarragona, um bocadinho depois das 10, 30 horas j� se saber� o nome do contemplado.\n" +
                "FERNANDO CASEIRO EM TARRAGONA Como � seu h�bito, o Benfica n�o deixar� de estar devidamente representado, amanh�, em Tarragona, no sorteio da �Ta�a dos Clubes Campe�es Europeus�. Uma vez mais o encargo foi confiado ao dirigente Sr. Fernando Caseiro, que, al�m da representa��o, tratar� logo, sendo poss�vel, dos primeiros contactos com o delegado do clube que a sorte nos designar por advers�rio.\n" +
                "Agora que o pobre do Eus�bio tem andado a comer o p�o que o diabo amassou, amarrado � cama e aos tratamentos de muitos aparelh�metros, quando o seu maior desejo era andar, no campo, a tratar a bola por tu, com a sua indesment�vel mestria, agora que, finalmente, as coisas se apresentam de melhor cariz e o mo�o regressa � vida, vamos dedicar este nosso apontamento dominical justamente ao senhor Eus�bio da Silva, que, para ser bem portugu�s, at� tinha de ter o inconfund�vel Silva plantado no nome. Ser� uma hist�ria que, ainda por cima, estar� dentro das caracter�sticas desta sec��o, pois ocorreu num domingo � o outro � e para aqueles que t�m a preocupa��o da actualidade poderemos referir que ainda est� certa, porque Eus�bio, sem d�vida, foi o grande ausente de futebol do domingo passado. Mas vamos � hist�ria: nas desloca��es � prov�ncia, por vezes, nem tudo s�o facilidades, principalmente no transporte daqueles sacos imensos e pesados, que levam as equipas, as botas, as bolas, as toalhas, etc. e tal.\n" +
                "Por vezes, se as desloca��es s�o perto, seguem funcion�rios do clube para se ocuparem do transporte dos sacos entre o autocarro e as cabinas, mas se as idas s�o para longe, � evidente que seria economicamente indefens�vel, o levar-se pessoal simplesmente para esse fim. A norma, ent�o, � aproveitar os servi�os de alguns mancebos locais, que, a troco de alguns escudos, se disp�em a fazer um �gancho�, solucionando, assim, o problema. Mas, muitas vezes, nem os tais homens aparecem e ent�o o problema tem de ser resolvido pelos pr�prios meios. Estamos bem lembrados de um dia, em �vora, porque a nossa partida estava atrasada, ter visto Fernando Riera a levar um dos sacos... L� nisso, n�o h� fidalguias e todos andam para a frente. � claro, aquilo que para n�s � vulgar, para outros tem todo o aspecto de uma evidente surpresa. E essa surpresa aconteceu, precisamente no pen�ltimo domingo, em Viana do Castelo, quando a muita gente que estava � espera do Benfica, j� no campo do Vianense, se admirou de ver o Eus�bio, o n.� 2 da Europa, o mais popular jogador portugu�s, o �maior� da equipa, modestamente, sem alardes de grande senhor, com o saquinho, (que pesa, amigos, que pesa... ), ajoujado, a fazer de �manel das equipas�. O facto parece sem significado de maior, mas, muito pelo contr�rio, � bastante representativo da simplicidade que reina na equipa do Benfica, segredo das suas ";

        text = LgteDiacriticFilter.clean(text);
        System.out.println(text);
        System.out.println(getHTML(text,"",""));
        text = "<DOC id=\"MMM_ENG_20020101.0001\" type=\"story\">\n<TEXT>\n" + text + "\n</TEXT>\n</DOC>";
        
        String title = "";
        Document dom = CallWebServices.callServices(text.replaceAll("<[^>]+>",""),title,0,0,0,"sdfdf","MMM_ENG_20020101.0001","en-EN");
        String html = Server.getHtml("test","teste","teste",text,"<doc id=\"MMM_ENG_20020101.0001\">\n" + nmaf.util.DomUtil.domToString(dom,false) + "\n</doc>" ,null);
        System.out.println(html);





    }

    public static String getHTML(String text,String keyworkds, String titulo) throws Exception
    {
        text = "<DOC id=\"MMM_ENG_20020101.0001\" type=\"story\">\n<TEXT>\n" + text + "\n</TEXT>\n</DOC>";
        Document dom = CallWebServices.callServices(text.replaceAll("<[^>]+>",""),titulo,0,0,0,"sdfdf","MMM_ENG_20020101.0001","en-EN");
        return Server.getHtml("MMM_ENG_20020101.0001",keyworkds,"",text,"<doc id=\"MMM_ENG_20020101.0001\">\n" + nmaf.util.DomUtil.domToString(dom,false) + "\n</doc>" ,null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if(request.getParameter("texto") != null)
        {

            String text = request.getParameter("texto");
            text = LgteDiacriticFilter.clean(text);
            String palavras = request.getParameter("palavras") != null ? request.getParameter("palavras") : "";

            if(request.getParameter("xml") != null && request.getParameter("xml").equals("true"))
            {
                try {
                    Document dom = CallWebServices.callServices(text,"",0,0,0,"sdfdf","MMM_ENG_20020101.0001","en-EN");
                    String xml = DomUtil.domToString(dom,true);
                    response.setContentType("text/html");
                    out.print(xml);
                } catch (Exception e) {
                    out.print(e.toString());
                }
            }
            else
            {
            try {
                String html = getHTML(text,palavras,"");
                response.setContentType("text/html");
                html = "" +
                        "<html>" +
                        "<head>" +
                        "\t<link rel=\"Shortcut icon\" href=\"enterprise/images/favicon.ico\" type=\"image/x-icon\" />\n" +
                        "\t  <script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAADxvyl7Xh60gdXI_1zKmmGRQ45p7OlXlrLkk7yZOk6cwGipt7LhSf0Cse0G8kMatFg8dl2HwefKZJgA&sensor=false\"\n" +
                        "            type=\"text/javascript\"></script>\n" +
                        "\t<script src=\"scripts.js\" type=\"text/javascript\"></script>\n" +
                        "            " +
                        " <link rel=\"Shortcut icon\" href=\"/modiplace/enterprise/images/favicon.ico\" type=\"image/x-icon\" />\n" +
                        "<style>" +
                        "label.PLACE\n" +
                        "{\n" +
                        "\tbackground-color:lightcoral;\n" +
                        "}\n" +
                        "\n" +
                        ".warning\n" +
                        "{\n" +
                        "\tbackground-color:yellow;\n" +
                        "}\n" +
                        "\n" +
                        "label.TIMEX\n" +
                        "{\n" +
                        "\tbackground-color:lightskyblue;\n" +
                        "}\n" +
                        "label.word\n" +
                        "{\n" +
                        "\tbackground-color:yellow;\n" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body onload=\"initialize()\" onunload=\"GUnload()\">" +
                        html +
                         "<script type=\"text/javascript\">\n" +
                        "\n" +
                        "\tvar map;\n" +
                        "\tvar gmarker;\n" +
                        "\t\n" +
                        "    function initialize() {\n" +
                        "      if (GBrowserIsCompatible()) {\n" +
                        "        map = new GMap2(document.getElementById(\"map_canvas\"));\n" +
                        "        map.setCenter(new GLatLng(37.4419, -122.1419), 13);\n" +
                        "        //map.setUIToDefault();\n" +
                        "\t\tgmarker = new GMarker(new GLatLng(32.4419, -9.1419));\n" +
                        "\t\tmap.addOverlay(gmarker);\n" +
                        "      }\n" +
                        "    }\n" +
                        "\tfunction closeMapa(){\n" +
                        "\t\t\n" +
                        "\t\t//getObjectById('contentor').style.display=\"none\";\n" +
                        "\t\tgetObjectById('map_canvas').style.display=\"none\";\n" +
                        "\t\t \n" +
                        "\t}\n" +
                        "\n" +
                        "\tfunction centraMapa() {\n" +
                        "\t\tvar point = new GLatLng(32.4419, -9.1419);\n" +
                        "\t\tmap.setCenter(point,4);\n" +
                        "\t}\n" +
                        "\tfunction abreMapa(lat,lng) {\n" +
                        "\t  //getObjectById('contentor').style.display=\"\";\n" +
                        "\t  getObjectById('map_canvas').style.display=\"\";\n" +
                        "\t  \n" +
                        "\t  \n" +
                        "\t  \n" +
                        "\t  var point = new GLatLng(lat,lng);\n" +
                        "\t  gmarker.setLatLng(point);\n" +
                        "\t  map.addOverlay(new GMarker(point));\n" +
                        "\t  map.setCenter(point,4);\n" +
                        "\t  \n" +
                      
                        "\t}\n" +
                        "\n" +
                        "    </script>\n" +
                        "\t  <a href=\"javascript:abreMapa(32,-9)\">TESTE</a> | <a href=\"javascript:centraMapa()\">CENTRA</a> | <a href=\"javascript:closeMapa();\">Fechar Mapa</a>\n" +
                        "\t  \n" +
                        "\t\n" +
                        "\t\t<div id=\"map_canvas\" style=\"position:absolute; right:20px; top:20px;width: 500px; height: 300px;\"></div>" +
                        "</body>" +
                        "</html>";
                out.write("<html>");
                out.write(html);
            } catch (Exception e)
            {
                out.write(e.toString());
                e.printStackTrace();
            }
            }
//          org.dom4j.Document dom4j = pt.utl.ist.lucene.utils.Dom4jUtil.parse(nmaf.util.DomUtil.domToString(dom,true));


        }
        else
        {
            out.print("<?xml version=\"1.0\"?>\n<error/>");
        }
    }
}
