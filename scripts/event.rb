@now = Time.now.strftime("%Y-%m-%d %H:%M:%S.%3N")
EVENTS_FILE = "/opt/cash-monitor/files/events.txt"


def create_session
	"sesion_creada,#{@now}"
end

def coin_insert(count)
	"moneda_insertada,#{@now},#{count}"
end

def counter_reset()
	"contador_reseteado,#{@now}"
end

def update_config(coin_value, coin_count)
	"configuracion_actualizada,#{@now},#{coin_value},#{coin_count}"
end

def close_session
	"sesion_cerrada,@now"
end

def usage
	"event.rb <event_name> [<arg1>...<argn>]"
end

if ARGV.length > 0
	command_name = ARGV.shift
	command_args = ARGV.join(",")
	command_str = eval "#{command_name}(#{command_args})"
	system "echo #{command_str} | tee -a #{EVENTS_FILE}"
else
	usage
end
