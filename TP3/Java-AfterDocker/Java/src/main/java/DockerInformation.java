import com.github.dockerjava.api.command.DockerCmd;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;

import com.github.dockerjava.api.command.CreateContainerResponse;

public class DockerInformation {
    DockerClient dockerClient;

    // construtor que inicializa o cliente para comunicar com o docker
    public DockerInformation() throws DockerCertificateException {
        dockerClient = DefaultDockerClient.fromEnv().build();
    }


    public void createcontainer (String Image) throws DockerException, InterruptedException  {
        dockerClient.pull(Image);
        final String[] ports = {"80", "22"};
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>();
            hostPorts.add(PortBinding.of("127.0.0.1", port));
            portBindings.put(port, hostPorts);
        }
        //criacao de containers
       final ContainerConfig containerConfig = ContainerConfig.builder()
               .image(Image)
               .exposedPorts(ports)
               .build();
        final ContainerCreation container = dockerClient.createContainer(containerConfig);
        dockerClient.startContainer(container.id());

    }
    // lista dos nomes dos containers
    public TreeMap<Integer, String> getContainersNames() throws DockerException, InterruptedException {

        List<Container> allContainers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
        TreeMap<Integer, String> containers = new TreeMap<Integer, String>();

        for(int i = 0; i != allContainers.size(); i++){
            ContainerInfo info = dockerClient.inspectContainer(allContainers.get(i).id());
            containers.put(i+1, info.name());
        }

        return containers;
    }

    // lista dos status dos containers - 4
    public TreeMap<Integer, String> getContainersStatuses() throws DockerException, InterruptedException {

        List<Container> allContainers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
        TreeMap<Integer, String> statuses = new TreeMap<Integer, String>();

        for(int i = 0; i != allContainers.size(); i++){
            ContainerInfo info = dockerClient.inspectContainer(allContainers.get(i).id());
            statuses.put(i+1, info.state().status());
        }

        return statuses;
    }
    public TreeMap<Integer, String> getname() throws DockerException, InterruptedException {

        List<Container> allContainers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
        TreeMap<Integer, String> statuses = new TreeMap<Integer, String>();

        for(int i = 0; i != allContainers.size(); i++){
            ContainerInfo info = dockerClient.inspectContainer(allContainers.get(i).id());
            statuses.put(i+1, info.image());
        }

        return statuses;
    }
   // este é o 3
    public TreeMap<Integer, String> getImage() throws DockerException, InterruptedException {

        List<Container> allContainers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
        TreeMap<Integer, String> statuses = new TreeMap<Integer, String>();

        for(int i = 0; i != allContainers.size(); i++){
            ContainerInfo info = dockerClient.inspectContainer(allContainers.get(i).id());
            statuses.put(i+1, info.config().image());
        }

        return statuses;
    }
    // lista dos status dos containers
    public TreeMap<Integer, String> getContainersProcessor() throws DockerException, InterruptedException {

        List<Container> allContainers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
        TreeMap<Integer, String> statuses = new TreeMap<Integer, String>();

        for(int i = 0; i != allContainers.size(); i++){
            ContainerStats stats = dockerClient.stats(allContainers.get(i).id());
            statuses.put(i+1, stats.cpuStats().cpuUsage().totalUsage().toString());
        }

        return statuses;
    }


    public void closeDockerClient(){
        dockerClient.close();
    }
}
