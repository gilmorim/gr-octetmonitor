README
======

EXERCICIO SOBRE CONSTRUÇÃO DE MIB 
---------------------------------

1. Instalar os packages necessarios

    $ sudo apt-get install smitools snmpsim snmp-mibs-downloader

2. Criar e editar ficheiro com a MIB a partir de exemplo

    $ mkdir exercicioRFID
    $ cd exercicioRFID/
    $ wget http://net-snmp.sourceforge.net/docs/mibs/NET-SNMP-EXAMPLES-MIB.txt
    $ cp NET-SNMP-EXAMPLES-MIB.txt EXERCICIO-RFID-MIB.txt
    $ vim EXERCICIO-RFID-MIB.txt
      ... editar o conteudo em conformidade com Vim ou outro editor ...

3. Validacao sintatica

    $ smilint EXERCICIO-RFID-MIB.txt
    ... eliminar os erros... um a um... editar, compilar, editar, compular...

    ... tratar tambem dos warnings (opcional) ...
    $ smilint -s -m -l 6 EXERCICIO-RFID-MIB.txt
    ...

4. Gerar dados para simular a MIB

    $ mib2dev.py --mib-source . --mib-source http://mibs.snmplabs.com/asn1/@mib@ --output-file=./data/rfid.snmprec --table-size=3 --mib-module=EXERCICIO-RFID-MIB
    ... vai produzir um ficheiro na pasta ./data com o ome rfid.snmprec

    (em alternativa usar o ficheiro de dados fornecido, baseado no enunciado)
    $ cat ./data/rfid.snmprec

        1.3.6.1.3.2018.1.0|4|ZONA DE MONTAGEM
        1.3.6.1.3.2018.2.0|4|LEITOR RFID MONTAGEM
        1.3.6.1.3.2018.3.0|64x|69266f6e
        1.3.6.1.3.2018.4.0|2|3
        1.3.6.1.3.2018.5.1.1.1|2|1
        1.3.6.1.3.2018.5.1.1.2|2|2
        1.3.6.1.3.2018.5.1.1.3|2|3
        1.3.6.1.3.2018.5.1.2.1|4|222222
        1.3.6.1.3.2018.5.1.2.2|4|111111
        1.3.6.1.3.2018.5.1.2.3|4|000000
        1.3.6.1.3.2018.5.1.3.1|4|Motor
        1.3.6.1.3.2018.5.1.3.2|4|Travoes
        1.3.6.1.3.2018.5.1.3.3|4|Caixa
        1.3.6.1.6.3.10.2.1.1.0|4|Agente de teste da Oficina
        1.3.6.1.6.3.10.2.1.2.0|2|10
        1.3.6.1.6.3.10.2.1.3.0|2|60
        1.3.6.1.6.3.10.2.1.4.0|2|2048

5. Simular o agente com a MIB gerada

    $ snmpsimd.py --data-dir=./data --agent-udpv4-endpoint=127.0.0.1:1024

6. Testar com snmpwalk


    $ snmpwalk -v2c -c rfid localhost:1024 experimental.2018
        SNMPv2-SMI::experimental.2018.1.0 = STRING: "ZONA DE MONTAGEM"
        SNMPv2-SMI::experimental.2018.2.0 = STRING: "LEITOR RFID MONTAGEM"
        SNMPv2-SMI::experimental.2018.3.0 = IpAddress: 105.38.111.110
        SNMPv2-SMI::experimental.2018.4.0 = INTEGER: 3
        SNMPv2-SMI::experimental.2018.5.1.1.1 = INTEGER: 1
        SNMPv2-SMI::experimental.2018.5.1.1.2 = INTEGER: 2
        SNMPv2-SMI::experimental.2018.5.1.1.3 = INTEGER: 3
        SNMPv2-SMI::experimental.2018.5.1.2.1 = STRING: "222222"
        SNMPv2-SMI::experimental.2018.5.1.2.2 = STRING: "111111"
        SNMPv2-SMI::experimental.2018.5.1.2.3 = STRING: "000000"
        SNMPv2-SMI::experimental.2018.5.1.3.1 = STRING: "Motor"
        SNMPv2-SMI::experimental.2018.5.1.3.2 = STRING: "Travoes"
        SNMPv2-SMI::experimental.2018.5.1.3.3 = STRING: "Caixa"
        SNMPv2-SMI::experimental.2018.5.1.3.3 = No more variables left in this MIB

     $ snmpbulkget -v2c -c rfid localhost:1024 1

         SNMPv2-SMI::experimental.2018.1.0 = STRING: "ZONA DE MONTAGEM"
         SNMPv2-SMI::experimental.2018.2.0 = STRING: "LEITOR RFID MONTAGEM"
         SNMPv2-SMI::experimental.2018.3.0 = IpAddress: 105.38.111.110
         SNMPv2-SMI::experimental.2018.4.0 = INTEGER: 3
         SNMPv2-SMI::experimental.2018.5.1.1.1 = INTEGER: 1
         SNMPv2-SMI::experimental.2018.5.1.1.2 = INTEGER: 2
         SNMPv2-SMI::experimental.2018.5.1.1.3 = INTEGER: 3
         SNMPv2-SMI::experimental.2018.5.1.2.1 = STRING: "222222"
         SNMPv2-SMI::experimental.2018.5.1.2.2 = STRING: "111111"
         SNMPv2-SMI::experimental.2018.5.1.2.3 = STRING: "000000"
         SNMPv2-SMI::experimental.2018.5.1.3.1 = STRING: "Motor"
         SNMPv2-SMI::experimental.2018.5.1.3.2 = STRING: "Travoes"
         SNMPv2-SMI::experimental.2018.5.1.3.3 = STRING: "Caixa"
         SNMP-FRAMEWORK-MIB::snmpEngineID.0 = STRING: "Agente de teste da Oficina"
         SNMP-FRAMEWORK-MIB::snmpEngineBoots.0 = INTEGER: 10
         SNMP-FRAMEWORK-MIB::snmpEngineTime.0 = INTEGER: 60 seconds
         SNMP-FRAMEWORK-MIB::snmpEngineMaxMessageSize.0 = INTEGER: 2048
         SNMP-FRAMEWORK-MIB::snmpEngineMaxMessageSize.0 = No more variables left in
         this MIB View (It is past the end of the MIB tree)



