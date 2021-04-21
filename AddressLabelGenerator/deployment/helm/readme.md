# HELM Template

This template should be used as a base when we start deploying new services to kubernetes.

It offers the following pre defined features:

* Deployment
* Ingress for K8s
* Ingressroute for Traefik
* Service deployment
* Prometheus monitoring

## Usage
Download the repository as ZIP or TAR.GZ and place its content into ./`deployment/helm` within your repository.

Afterwards adapt atleast the files `Chart.yaml` and `values.yaml`.
If you need any further configutration put the additional files into the folder `./templates`. 

Also feel free to adapt the existing files to your needs, but you should take care to use variables filled from `values.yaml` for all parameters which can differ in each environment.

This template expects the variable "CI_ENVIRONMENT_SLUG" to be set, which is done automatically in gitlab-ci, for a better identification of the deployments in the cluster.

As last step add the deployment to your CI pipeline.

Here is an example for Gitlab (This expects that you have added your Kubernetes Cluster to your project or group)

```
de-bai1-production:
  image:
    name: alpine/helm:3.1.2
    entrypoint: [""]
  stage: deploy
  environment:
    name: de-bai1-production
    url: https://myproject.brodos.net
    kubernetes:
            namespace: <target-namespace>
  when: manual
  script:
    - |
        helm upgrade --install --wait MyFancyProject ./deployment/helm \
        --set ciEnvironmentSlug="${CI_ENVIRONMENT_SLUG}" \
        --set env.MYENVVAR="${MYENVVAR}" \
        --set image.tag="${CI_COMMIT_REF_NAME}"

```

## Sub Charts
Helm allows you to put additional charts into the same chart. Those are so called subcharts (https://helm.sh/docs/chart_template_guide/subcharts_and_globals/).

Subcharts are always deployed with your mainchart and should be only used if your main chart always requires a independet deployment of the sub chart.
If several versions of your main deployment can share a service which would be deployed by the subchart you should more likely consider to deploy the subchart as and independent chart deployment.



