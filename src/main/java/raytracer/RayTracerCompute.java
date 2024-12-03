package raytracer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.lwjgl.opengl.GL43.*;

public class RayTracerCompute {
    private final int program;

    public RayTracerCompute() {
        int computeShader = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(computeShader, readSource("src/main/resources/raytracer.comp"));
        glCompileShader(computeShader);

        if (glGetShaderi(computeShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Failed to compile compute shader:\n" + glGetShaderInfoLog(computeShader));
        }

        program = glCreateProgram();
        glAttachShader(program, computeShader);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Failed to link program:\n" + glGetProgramInfoLog(program));
        }

        glDeleteShader(computeShader);
    }

    public void compute(int width, int height) {
        glUseProgram(program);
        glDispatchCompute((int) Math.ceil(width / 8f), (int) Math.ceil(height / 4f), 1);
        glMemoryBarrier(GL_ALL_BARRIER_BITS);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteProgram(program);
    }

    private static String readSource(String path) {
        File file = new File(path);

        try (Scanner scanner = new Scanner(file)) {
            StringBuilder source = new StringBuilder();

            while (scanner.hasNextLine()) {
                source.append(scanner.nextLine()).append('\n');
            }

            return source.toString();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }
}