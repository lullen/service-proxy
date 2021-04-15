job "app-job" {
  datacenters = ["dc1"]
  group "server" {
    count = 1
    network {
      port "http-dapr" {
        static = 50001 # dapr port
      }
      port "http-server" { }
    }
    task "server" {
      driver = "docker"
      config {
        image = "lullen/javaserver"
        ports = ["http-server"]
      }
    }
    task "dapr-server" {
      driver = "docker"
      config {
        image = "daprio/daprd:edge"
        ports = ["http-dapr"]
        args  = [
          "./daprd",
          "-app-id", "server",
          "-app-protocol", "grpc",
          "-app-port", "5000"
        ]
        # network_mode = "container:server-${NOMAD_ALLOC_ID}"
      }
    }
  }
  group "client" {
    count = 1
    network {
      port "http-dapr" {
        static = 50001 # dapr port
      }
      port "http-client" { }
    }
    task "client" {
      driver = "docker"
      config {
        image = "lullen/javaclient"
        ports = ["http-client"]
      }
    }
    task "dapr-client" {
      driver = "docker"
      config {
        image = "daprio/daprd:edge"
        ports = ["http-dapr"]
        args  = [
          "./daprd",
          "-app-id", "client",
          "-app-protocol", "grpc"
        ]
        # network_mode = "container:client-${NOMAD_ALLOC_ID}"
      }
    }
  }
}
