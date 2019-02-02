import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.Image;

import java.util.List;

public class Dockerfunctions  {
    DockerClient dockerClient;

    public Dockerfunctions() throws DockerCertificateException {
        dockerClient = DefaultDockerClient.fromEnv().build();
    }

    public void getContainerList() throws DockerException, InterruptedException {

        List<Container> allContainers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
        for(int i = 0; i !=allContainers.size(); i++){
            ContainerInfo info = dockerClient.inspectContainer(allContainers.get(i).id());
            String index = String.valueOf(i);
            String image = info.config().image();
            SingleTableImage TI = SingleTableImage.getInstance();
            TI.Put_ID_Image(index,image);
            TI.Put_size(allContainers.size());

        }

    }

    public void closeDockerClient(){
        dockerClient.close();
    }
}