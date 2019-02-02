
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.List;

public class DockerTest {

    public static void main(String[] args) {

        DefaultDockerClientConfig config
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withRegistryEmail("info@baeldung.com")
                .withRegistryPassword("baeldung")
                .withRegistryUsername("baeldung")
                .withDockerCertPath("C:\\Users\\Joca\\.docker\\machine\\machines\\default")
                .withDockerConfig("/home/baeldung/.docker/")
                .withDockerTlsVerify("1")
                .withDockerHost("tcp://192.168.99.100:2376").build();

        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        List<Image> images = dockerClient.listImagesCmd().exec();
        int i= 1;
        int j= 1;
        for (Image image: images){
            System.out.println("Image nrº"+i+" "+image);
            i++;
        }
        System.out.println("\n\n");
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true).exec();

        for (Container container: containers){
            System.out.println("Container nrº"+j+" "+container);
            j++;
        }


        InspectContainerResponse container2
                = dockerClient.inspectContainerCmd("blabla").exec();

        System.out.println("\n\n");

        System.out.println("IMAGE ID: "+container2.getImageId()+ "\n");
        System.out.println("IMAGE CONFIG: "+container2.getConfig()+ "\n");
        System.out.println("IMAGE PATH: "+container2.getPath()+ "\n");
    }


}